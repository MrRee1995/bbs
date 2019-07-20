package com.ccnu.bbs.service;

import com.ccnu.bbs.VO.ArticleVO;
import com.ccnu.bbs.entity.Article;
import com.ccnu.bbs.forms.ArticleForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;

public interface ArticleService {

    /** 查询帖子列表. */
    Page<ArticleVO> allArticle(Pageable pageable);

    /** 查询版块帖子列表. */
    Page<ArticleVO> topicArticle(Integer topicType, Pageable pageable);

    /** 搜索帖子. */
    Page<ArticleVO> searchArticle(String searchKey, Pageable pageable);

    /** 创建帖子. */
    Article createArticle(String userId, ArticleForm articleForm);

    /** 上传图片. */
    String uploadImg(File file) throws QiniuException;

    /** 删除图片. */
    Response deleteImg(String imgUrl) throws QiniuException;

    /** 浏览帖子. */
    ArticleVO findArticle(String articleId, String userId);

    /** 查找帖子. */
    Article getArticle(String articleId);

    /** 删除帖子. */
    void deleteArticle(String aritcleId);

    /** 查找用户发表的帖子. */
    Page<ArticleVO> findUserArticle(String userId, Pageable pageable);

    /** 查找用户收藏的帖子. */
    Page<ArticleVO> findCollectArticle(String userId, Pageable pageable);

    /** 从redis更新数据库. */
    void updateArticleDatabase();
}
