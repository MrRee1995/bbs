package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.VO.ArticleVO;
import com.ccnu.bbs.converter.Date2StringConverter;
import com.ccnu.bbs.entity.Article;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.enums.DeleteEnum;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.ArticleForm;
import com.ccnu.bbs.repository.*;
import com.ccnu.bbs.searchRepository.ArticleSearchRepository;
import com.ccnu.bbs.service.ArticleService;
import com.ccnu.bbs.utils.EntityUtils;
import com.ccnu.bbs.utils.KeyUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Math.E;


@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleSearchRepository articleSearchRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private CollectServiceImpl collectService;

    @Autowired
    private QiniuServiceImpl qiniuService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Double VIEW_NUM_WEIGHT = 1.0;
    private static final Double COMMENT_NUM_WEIGHT = 200.0;
    private static final Double LIKE_NUM_WEIGHT = 200.0;
    private static final Double INIT_VALUE =  100.0;

    @Override
    /**
     * 帖子列表
     */
    public Page<ArticleVO> allArticle(Pageable pageable) {
        // 1.查找出帖子列表并按热度排序
        Page<Article> articles = articleRepository.findAll(pageable);
        // 2.对每一篇帖子进行拼装
        List<ArticleVO> articleVOList = articles.stream().
                map(e -> article2articleVO(e, null)).collect(Collectors.toList());
        return new PageImpl(articleVOList, pageable, articles.getTotalElements());
    }

    @Override
    /**
     * 版块帖子列表
     */
    public Page<ArticleVO> topicArticle(Integer topicType, Pageable pageable){
        // 1.查找出特定版块帖子列表
        Page<Article> articles;
        // 如果是失物招领的版块则按创建时间倒序排序
        if (topicType == 3){
            articles = articleRepository.findAllByTopicTime(topicType, pageable);
        }
        else {
            articles = articleRepository.findAllByTopicHot(topicType, pageable);
        }
        // 2.对每一篇帖子进行拼装
        List<ArticleVO> articleVOList = articles.stream().
                map(e -> article2articleVO(e, null)).collect(Collectors.toList());
        return new PageImpl(articleVOList, pageable, articles.getTotalElements());
    }

    @Override
    /**
     * 帖子搜索！！
     */
    public Page<ArticleVO> searchArticle(String searchKey, Pageable pageable) {
        // 1.设置对应字段的权重分值
        // 创建一个FunctionScoreQueryBuilder.FilterFunctionBuilder对象数组
        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();
        filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("articleKeywords", searchKey),
                ScoreFunctionBuilders.weightFactorFunction(5)));
        filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("articleTitle", searchKey),
                ScoreFunctionBuilders.weightFactorFunction(5)));
        filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("articleContent", searchKey),
                ScoreFunctionBuilders.weightFactorFunction(2)));
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];
        filterFunctionBuilders.toArray(builders);
        // 将FunctionScoreQueryBuilder.FilterFunctionBuilder对象数组作为构造器参数传入
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders).
                scoreMode(FunctionScoreQuery.ScoreMode.SUM). //设定分值为分数之和
                setMinScore(5); //超过5分才查询
        // 已经删除的帖子不查询
        QueryBuilder queryBuilder = QueryBuilders.termQuery("articleIsDelete", 0);
        // 用boolQuery()构造多重查询条件
        QueryBuilder qb = QueryBuilders.boolQuery().must(functionScoreQueryBuilder).must(queryBuilder);
        // 创建搜索 DSL 查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(qb).
                withPageable(pageable).build();
//        log.info("\n searchArticle(): searchContent [" + searchKey + "] \n DSL  = \n " + searchQuery.getQuery().toString());
        // 搜索，获取结果
        Page<Article> articles = articleSearchRepository.search(searchQuery);
        // 2.对每一篇帖子进行拼装
        List<ArticleVO> articleVOList = articles.stream().
                map(e -> article2articleVO(e, null)).collect(Collectors.toList());
        return new PageImpl(articleVOList, pageable, articles.getTotalElements());
    }

    @Override
    /**
     * 创建帖子
     */
    @Transactional
    public Article createArticle(String userId, ArticleForm articleForm){
        Article article = new Article();
        if (!articleForm.getImgUrls().isEmpty()){
            // 1.获取图片url列表
            List<String> imgUrls = articleForm.getImgUrls();
            // 2.将字符串拼接存入帖子字段
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0 ; i < imgUrls.size(); i++){
                stringBuffer.append(imgUrls.get(i));
                if (i != imgUrls.size() - 1) stringBuffer.append(";");
            }
            // 3.将生成的图片url给帖子的图片字段
            article.setArticleImg(stringBuffer.toString());
        }
        // 4.将帖子其余信息保存
        BeanUtils.copyProperties(articleForm, article);
        article.setArticleId(KeyUtil.genUniqueKey());
        article.setArticleUserId(userId);
        // 5.计算帖子热度
        article = calcHotNum(article);
        // 6.将帖子存入数据库
        article = articleRepository.save(article);
        // 7.将帖子存入es,以便搜索
        article = articleSearchRepository.save(article);
        // 8.创建帖子需要从redis同步一遍帖子数据，以便排序
        updateArticleDatabase();
        // 9.将用户发帖数+1
        User user = userService.findUser(userId);
        user.setUserArticleNum(user.getUserArticleNum() + 1);
        userService.saveUser(user);
        return article;
    }

    @Override
    /**
     * 上传图片
     */
    public String uploadImg(File file) throws QiniuException {
        // 1.创建存储url的字符串
        String imgUrl = new String();
        // 2.判断文件是否存在
        if (!file.exists()){
            return imgUrl;
        }
        // 3.使用KeyUtil生成唯一主键作为key进行上传，返回图片url
        imgUrl = qiniuService.uploadFile(file, "bbs/" + KeyUtil.genUniqueKey() + "-web");
        return imgUrl;
    }

    @Override
    /**
     * 删除图片
     */
    public Response deleteImg(String imgUrl) throws QiniuException {
        String key = imgUrl.substring(25, imgUrl.length() - 4);
        return qiniuService.delete(key);
    }

    @Override
    /**
     * 浏览帖子
     */
    public ArticleVO findArticle(String articleId, String userId) throws BBSException {
        Article article = getArticle(articleId);
        ArticleVO articleVO;
        // 2.如果存在这篇帖子，将帖子浏览数+1，存入redis中
        redisTemplate.opsForHash().increment("Article::" + articleId, "articleViewNum", 1);
        article.setArticleViewNum(article.getArticleViewNum() + 1);
//        redisTemplate.opsForValue().set("Article::" + articleId, article, 1, TimeUnit.HOURS);

        articleVO = article2articleVO(article, userId);
        return articleVO;
    }

    @Override
    /**
     * 从redis中或数据库中查找帖子
     */
    public Article getArticle(String articleId) throws BBSException{
        Article article;
        if (redisTemplate.hasKey("Article::" + articleId)){
            article = EntityUtils.hashToObject(redisTemplate.opsForHash().entries("Article::" + articleId), Article.class);
        }
        else {
            article = articleRepository.findArticle(articleId);
            if (article == null){
                throw new BBSException(ResultEnum.ARTICLE_NOT_EXIT);
            }
            redisTemplate.opsForHash().putAll("Article::" + articleId, EntityUtils.objectToHash(article));
            redisTemplate.expire("Article::" + articleId, 1, TimeUnit.HOURS);
        }
        return article;
    }

    @Override
    /**
     * 删除帖子
     */
    @Transactional
    public void deleteArticle(String articleId) throws BBSException {
        // 1.获取到帖子
        Article article = getArticle(articleId);
        // 2.将删除位置为1
        article.setArticleIsDelete(DeleteEnum.DELETE.getCode());
        // 3.更新数据库及es并删除缓存
        redisTemplate.delete("Article::" + articleId);
        articleRepository.save(article);
        articleSearchRepository.save(article);
        // 4.用户帖子数-1
        User user = userService.findUser(article.getArticleUserId());
        user.setUserArticleNum(user.getUserArticleNum() - 1);
        userService.saveUser(user);
    }

    @Override
    /**
     * 查找用户发表的帖子
     */
    public Page<ArticleVO> findUserArticle(String userId, Pageable pageable) {
        // 1.查找用户发表的帖子
        Page<Article> articles = articleRepository.findUserArticle(userId, pageable);
        // 2.对每一篇帖子进行拼装
        List<ArticleVO> articleVOList = articles.stream().
                map(e -> article2articleVO(e, userId)).collect(Collectors.toList());
        return new PageImpl(articleVOList, pageable, articles.getTotalElements());
    }

    @Override
    /**
     * 查找用户收藏的帖子
     */
    public Page<ArticleVO> findCollectArticle(String userId, Pageable pageable) {
        collectService.updateCollectDatabase();
        // 1.查找用户收藏的帖子
        Page<Article> articles = articleRepository.findUserCollect(userId, pageable);
        // 2.对每一篇帖子进行拼装
        List<ArticleVO> articleVOList = articles.stream().
                map(e -> article2articleVO(e, null)).collect(Collectors.toList());
        return new PageImpl(articleVOList, pageable, articles.getTotalElements());
    }

    @Override
    /**
     * 从redis更新帖子数据
     */
    @Transactional
    public void updateArticleDatabase() {
        // 1.找到所有关于帖子的key
        Set<String> articleKeys = redisTemplate.keys("Article::*");
        for (String articleKey : articleKeys){
            // 2.根据每一个key得到帖子
            Article article = EntityUtils.hashToObject(redisTemplate.opsForHash().entries(articleKey), Article.class);
            // 3.更新帖子热度
            article = calcHotNum(article);
            // 4.保存帖子进数据库及es，再更新redis里的数据
            article = articleRepository.save(article);
            articleSearchRepository.save(article);
            redisTemplate.opsForHash().putAll(articleKey, EntityUtils.objectToHash(article));
            redisTemplate.expire(articleKey, redisTemplate.getExpire(articleKey), TimeUnit.SECONDS);
//            redisTemplate.delete(articleKey);
        }
        return;
    }

    /**
     * 计算帖子热度
     * @param article
     * @return
     */
    private Article calcHotNum(Article article){
        Double hotNum;
        if (article.getArticleCreateTime() != null){
            Double deltaTime = (System.currentTimeMillis() - article.getArticleCreateTime().getTime()) / 86400000.0;
            hotNum = (INIT_VALUE + LIKE_NUM_WEIGHT * article.getArticleLikeNum()
                    + VIEW_NUM_WEIGHT * article.getArticleViewNum()
                    + COMMENT_NUM_WEIGHT * article.getArticleCommentNum()) / Math.pow(E, deltaTime);
        }
        else {
            hotNum = (INIT_VALUE + LIKE_NUM_WEIGHT * article.getArticleLikeNum()
                    + VIEW_NUM_WEIGHT * article.getArticleViewNum()
                    + COMMENT_NUM_WEIGHT * article.getArticleCommentNum()) / Math.pow(E, 0);
        }
        article.setArticleHotNum(hotNum);
        return article;
    }

    /**
     * 文章内容拼装
     * @param article
     * @param userId
     * @return
     */
    private ArticleVO article2articleVO(Article article, String userId){
        ArticleVO articleVO = new ArticleVO();
        // 获得帖子信息
        BeanUtils.copyProperties(article, articleVO);
        // 查找作者信息
        User user = userService.findUser(article.getArticleUserId());
        BeanUtils.copyProperties(user, articleVO);
        // 查看作者身份
        articleVO.setUserRole(user.getUserRoleType());
        // 查看是不是当前用户所发帖子
        if (userId != null){
            articleVO.setIsOneself(userId.equals(user.getUserId()));
        }
        // 设定时间
        articleVO.setArticleCreateTime(Date2StringConverter.convert(article.getArticleCreateTime()));
        // 获得图片url
        if (article.getArticleImg() != null){
            articleVO.setArticleImages(Arrays.asList(article.getArticleImg().split(";")));
        }
        // 查找关键词信息
        if (article.getArticleKeywords() != null){
            articleVO.setKeywords(Arrays.asList(article.getArticleKeywords().split("[ ]+")));
        }
        // 查看帖子是否被当前用户点赞
        articleVO.setIsLike(likeService.isArticleLike(article.getArticleId(), userId));
        // 查看帖子是否被当前用户收藏
        articleVO.setIsCollect(collectService.isArticleCollect(article.getArticleId(), userId));
        // 查看帖子是否被删除
        articleVO.setIsDelete(article.getArticleIsDelete().equals(DeleteEnum.DELETE.getCode()));
        return articleVO;
    }

}
