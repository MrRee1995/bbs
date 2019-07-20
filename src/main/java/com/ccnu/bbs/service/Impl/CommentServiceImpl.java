package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.VO.CommentVO;
import com.ccnu.bbs.VO.ReplyVO;
import com.ccnu.bbs.converter.Date2StringConverter;
import com.ccnu.bbs.entity.*;
import com.ccnu.bbs.enums.DeleteEnum;
import com.ccnu.bbs.enums.MessageEnum;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.CommentForm;
import com.ccnu.bbs.repository.CommentRepository;
import com.ccnu.bbs.service.CommentService;
import com.ccnu.bbs.utils.EntityUtils;
import com.ccnu.bbs.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ReplyServiceImpl replyService;

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    /**
     * 热评列表
     */
    public List<CommentVO> hotArticleComment(String userId, String articleId){
        // 1.根据帖子id查询评论，并按照点赞数降序排列(取前3的评论)
        List<Comment> comments = commentRepository.findArticleCommentByLike(articleId);
        // 2.对每一个评论加入评论作者信息和回复信息
        List<CommentVO> commentVOList = comments.stream().
                map(e -> comment2commentVO(userId, e)).collect(Collectors.toList());
        return commentVOList;
    }

    @Override
    /**
     * 查询帖子评论列表
     */
    public Page<CommentVO> articleComment(String userId, String articleId, Pageable pageable) {
        // 1.根据帖子id查询评论，并按照评论时间升序排列
        Page<Comment> comments = commentRepository.findArticleCommentByTime(articleId, pageable);
        // 2.对每一个评论加入评论作者信息和回复信息
        List<CommentVO> commentVOList = comments.stream().
                map(e -> comment2commentVO(userId, e)).collect(Collectors.toList());
        return new PageImpl(commentVOList, pageable, comments.getTotalElements());
    }

    @Override
    /**
     * 创建评论
     */
    @Transactional
    public Comment createComment(String userId, CommentForm commentForm) throws BBSException{
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentForm, comment);
        // 1.设置主键
        comment.setCommentId(KeyUtil.genUniqueKey());
        // 2.设置文章id
        comment.setCommentArticleId(commentForm.getArticleId());
        // 3.设置评论者
        comment.setCommentUserId(userId);
        comment = commentRepository.save(comment);
        // 4.在redis或数据库中查找帖子
        Article article = articleService.getArticle(comment.getCommentArticleId());
        // 5.将帖子评论数+1，存入redis中
        if (article != null){
            redisTemplate.opsForHash().increment("Article::" + article.getArticleId(), "articleCommentNum", 1);
//            article.setArticleCommentNum(article.getArticleCommentNum() + 1);
//            redisTemplate.opsForValue().set("Article::" + comment.getCommentArticleId(), article, 1, TimeUnit.HOURS);
        }
        updateCommentDatabase();
        articleService.updateArticleDatabase();
        // 6.如果不是自己的帖子，则创建评论消息，以通知被评论者
        if (!userId.equals(article.getArticleUserId())){
            Message message = new Message();
            message.setArticleId(article.getArticleId());
            message.setCommentId(comment.getCommentId());
            message.setMessageType(MessageEnum.REPLY_MESSAGE.getCode());
            message.setReceiverUserId(article.getArticleUserId());
            message.setSenderUserId(userId);
            message.setRepliedContent(article.getArticleTitle());
            message.setMessageContent(comment.getCommentContent());
            messageService.createMessage(message);
        }
        return comment;
    }

    @Override
    /**
     * 查看评论
     */
    public CommentVO findComment(String commentId, String userId) throws BBSException{
        Comment comment = getComment(commentId);
        CommentVO commentVO = comment2commentVO(userId, comment);
        return commentVO;
    }

    @Override
    /**
     * 从redis或数据库中得到评论
     */
    public Comment getComment(String commentId) throws BBSException {
        Comment comment;
        if (redisTemplate.hasKey("Comment::" + commentId)){
            comment = EntityUtils.hashToObject(redisTemplate.opsForHash().entries("Comment::" + commentId), Comment.class);
        }
        else {
            comment = commentRepository.findComment(commentId);
            if (comment == null){
                throw new BBSException(ResultEnum.COMMENT_NOT_EXIT);
            }
            redisTemplate.opsForHash().putAll("Comment::" + commentId, EntityUtils.objectToHash(comment));
            redisTemplate.expire("Comment::" + commentId, 1, TimeUnit.HOURS);
        }
        return comment;
    }

    @Override
    /**
     * 从redis更新评论数据
     */
    @Transactional
    public void updateCommentDatabase() {
        // 1.找到所有关于评论的key
        Set<String> commentKeys = redisTemplate.keys("Comment::*");
        for (String commentKey : commentKeys){
            // 2.根据每一个key得到评论
            Comment comment = EntityUtils.hashToObject(redisTemplate.opsForHash().entries(commentKey), Comment.class);
            // 4.保存帖子进数据库，并删除redis里的数据
            commentRepository.save(comment);
//            redisTemplate.delete(commentKey);
        }
        return;
    }

    /**
     * 评论内容拼装
     * @param userId
     * @param comment
     * @return
     */
    CommentVO comment2commentVO(String userId, Comment comment){

        CommentVO commentVO = new CommentVO();
        // 获得评论信息
        BeanUtils.copyProperties(comment, commentVO);
        // 查找帖子信息,查看帖子是否被删除
        Article article = articleService.getArticle(comment.getCommentArticleId());
        commentVO.setIsArticleDelete(article.getArticleIsDelete().equals(DeleteEnum.DELETE.getCode()));
        // 查找作者信息
        User user = userService.findUser(comment.getCommentUserId());
        BeanUtils.copyProperties(user, commentVO);
        // 查找作者身份
        commentVO.setUserRole(user.getUserRoleType());
        // 查看是否是当前用户所发评论
        commentVO.setIsOneself(userId.equals(user.getUserId()));
        // 查找回复信息
        List<ReplyVO> replies = replyService.commentReply(userId, comment.getCommentId(), PageRequest.of(0, 3)).getContent();
        commentVO.setReplies(replies);
        // 查看评论是否被当前用户点赞
        commentVO.setIsLike(likeService.isCommentLike(comment.getCommentId(), userId));
        // 设定时间
        commentVO.setCommentTime(Date2StringConverter.convert(comment.getCommentTime()));
        return commentVO;
    }
}
