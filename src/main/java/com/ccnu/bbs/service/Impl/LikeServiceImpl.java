package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.entity.*;
import com.ccnu.bbs.enums.LikeEnum;
import com.ccnu.bbs.enums.MessageEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.LikeArticleForm;
import com.ccnu.bbs.forms.LikeCommentForm;
import com.ccnu.bbs.repository.LikeArticleRepository;
import com.ccnu.bbs.repository.LikeCommentRepository;
import com.ccnu.bbs.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeArticleRepository likeArticleRepository;

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MessageServiceImpl messageService;

    @Override
    /**
     * 查看帖子是否被点赞
     */
    public Boolean isArticleLike(String articleId, String userId) {
        LikeArticle likeArticle;
        if (redisTemplate.hasKey("LikeArticle::" + articleId + '-' + userId)){
            likeArticle = (LikeArticle) redisTemplate.opsForValue().get("LikeArticle::" + articleId + '-' + userId);
        }
        else {
            likeArticle = likeArticleRepository.findLikeArticle(articleId, userId);
        }
        if (likeArticle != null){
            redisTemplate.opsForValue().set("LikeArticle::" + articleId + '-' + userId, likeArticle, 1, TimeUnit.HOURS);
            return likeArticle.getIsLike().equals(LikeEnum.LIKE.getCode());
        }
        else return false;
    }

    @Override
    /**
     * 查看评论是否被点赞
     */
    public Boolean isCommentLike(String commentId, String userId) {
        LikeComment likeComment;
        if (redisTemplate.hasKey("LikeComment::" + commentId + '-' + userId)){
            likeComment = (LikeComment) redisTemplate.opsForValue().get("LikeComment::" + commentId + '-' + userId);
        }
        else {
            likeComment = likeCommentRepository.findLikeComment(commentId, userId);
        }
        if (likeComment != null){
            redisTemplate.opsForValue().set("LikeComment::" + commentId + '-' + userId, likeComment, 1, TimeUnit.HOURS);
            return likeComment.getIsLike().equals(LikeEnum.LIKE.getCode());
        }
        else return false;
    }

    @Override
    /**
     * 帖子点赞更新
     */
    public LikeArticle updateLikeArticle(LikeArticleForm likeArticleForm, String userId) throws BBSException {
        LikeArticle likeArticle;
        String articleId = likeArticleForm.getLikeArticleId();
        // 1.在redis或数据库中查找被点赞的帖子
        Article article = articleService.getArticle(articleId);
        // 2.看redis中有没有帖子点赞信息
        if (redisTemplate.hasKey("LikeArticle::" + articleId + '-' + userId)){
            likeArticle = (LikeArticle) redisTemplate.opsForValue().get("LikeArticle::" + articleId + '-' + userId);
        }
        // 3.若没有则去数据库中查询点赞信息
        else {
            likeArticle = likeArticleRepository.findLikeArticle(articleId, userId);
        }
        // 4.若还是没有则新建点赞信息,并设置帖子id和点赞用户id
        if (likeArticle == null){
            likeArticle = new LikeArticle();
            likeArticle.setLikeArticleId(articleId);
            likeArticle.setLikeUserId(userId);
            // 如果是新建的点赞信息，并且点赞的状态是已点赞，而且点赞的不是自己的帖子，则创建新的点赞通知消息
            if (likeArticleForm.getIsLike().equals(LikeEnum.LIKE.getCode())
                    && !userId.equals(article.getArticleUserId())){
                Message message = new Message();
                message.setArticleId(articleId);
                message.setMessageType(MessageEnum.LIKE_MESSAGE.getCode());
                message.setReceiverUserId(article.getArticleUserId());
                message.setSenderUserId(userId);
                message.setRepliedContent(article.getArticleContent());
                message.setMessageContent("赞了你的帖子");
                messageService.createMessage(message);
            }
        }
        // 5.更新被点赞帖子的点赞数
        // 如果点赞信息为空或者为未点赞且要更新的点赞信息为已点赞，则帖子点赞数+1
        if (likeArticle.getIsLike() == null || likeArticle.getIsLike().equals(LikeEnum.NOT_LIKE.getCode())){
            if (likeArticleForm.getIsLike() == LikeEnum.LIKE.getCode()){
                redisTemplate.opsForHash().increment("Article::" + articleId, "articleLikeNum", 1);
            }
        }
        // 如果点赞信息为已点赞且要更新的点赞信息为未点赞，则帖子点赞数-1
        else {
            if (likeArticleForm.getIsLike().equals(LikeEnum.NOT_LIKE.getCode())){
                redisTemplate.opsForHash().increment("Article::" + articleId, "articleLikeNum", -1);
            }
        }
        // 5.设置点赞状态
        likeArticle.setIsLike(likeArticleForm.getIsLike());
        // 6.将点赞信息存入redis
        redisTemplate.opsForValue().set("LikeArticle::" + articleId + '-' + userId, likeArticle, 1, TimeUnit.HOURS);
        return likeArticle;
    }

    @Override
    /**
     * 评论点赞更新
     */
    public LikeComment updateLikeComment(LikeCommentForm likeCommentForm, String userId) throws BBSException {
        LikeComment likeComment;
        String commentId = likeCommentForm.getLikeCommentId();
        // 1.在redis或数据库中找到评论
        Comment comment = commentService.getComment(commentId);
        // 2.先看redis中有没有评论点赞信息
        if (redisTemplate.hasKey("LikeComment::" + commentId + '-' + userId)){
            likeComment = (LikeComment) redisTemplate.opsForValue().get("LikeComment::" + commentId + '-' + userId);
        }
        // 3.若没有则去数据库中查询点赞信息
        else {
            likeComment = likeCommentRepository.findLikeComment(commentId, userId);
        }
        // 4.若还是没有则新建点赞信息,并设置帖子id和点赞用户id
        if (likeComment == null){
            likeComment = new LikeComment();
            likeComment.setLikeCommentId(commentId);
            likeComment.setLikeUserId(userId);
            // 如果是新建的点赞信息，并且点赞的状态是已点赞，而且点赞的不是自己的评论，则创建新的点赞通知消息
            if (likeCommentForm.getIsLike().equals(LikeEnum.LIKE.getCode())
                    && !userId.equals(comment.getCommentUserId())){
                Message message = new Message();
                message.setArticleId(comment.getCommentArticleId());
                message.setMessageType(MessageEnum.LIKE_MESSAGE.getCode());
                message.setReceiverUserId(comment.getCommentUserId());
                message.setSenderUserId(userId);
                message.setRepliedContent(comment.getCommentContent());
                message.setMessageContent("赞了你的评论");
                messageService.createMessage(message);
            }
        }
        // 5.更新被点赞评论的点赞数
        // 如果点赞信息为空或者为未点赞且要更新的点赞信息为已点赞，则评论点赞数+1
        if (likeComment.getIsLike() == null || likeComment.getIsLike().equals(LikeEnum.NOT_LIKE.getCode())){
            if (likeCommentForm.getIsLike() == LikeEnum.LIKE.getCode()){
                redisTemplate.opsForHash().increment("Comment::" + commentId, "commentLikeNum", 1);
            }
        }
        // 如果点赞信息为已点赞且要更新的点赞信息为未点赞，则帖子点赞数-1
        else {
            if (likeCommentForm.getIsLike().equals(LikeEnum.NOT_LIKE.getCode())){
                redisTemplate.opsForHash().increment("Comment::" + commentId, "commentLikeNum", -1);
            }
        }
        // 6.设置点赞状态
        likeComment.setIsLike(likeCommentForm.getIsLike());
        // 7.将点赞信息存入redis
        redisTemplate.opsForValue().set("LikeComment::" + commentId + '-' + userId, likeComment, 1, TimeUnit.HOURS);
        return likeComment;
    }

    @Override
    /**
     * 从redis更新帖子点赞到数据库
     */
    @Transactional
    public void updateLikeArticleDatabase() {
        // 1.找到所有有关收藏的key
        Set<String> likeArticleKeys = redisTemplate.keys("LikeArticle::*");
        // 2.保存数据到数据库并清除redis中数据
        for (String likeArticleKey : likeArticleKeys){
            LikeArticle likeArticle = (LikeArticle) redisTemplate.opsForValue().get(likeArticleKey);
            likeArticle = likeArticleRepository.save(likeArticle);
            // 注意redis中的数据没有主键，必须存一次数据库有了主键后再存redis
            redisTemplate.opsForValue().set(likeArticleKey, likeArticle, redisTemplate.getExpire(likeArticleKey), TimeUnit.SECONDS);
//            redisTemplate.delete(likeArticleKey);
        }
        return;
    }

    @Override
    /**
     * 从redis更新评论点赞到数据库
     */
    public void updateLikeCommentDatabase() {
        // 1.找到所有有关收藏的key
        Set<String> likeCommentKeys = redisTemplate.keys("LikeComment::*");
        // 2.保存数据到数据库并清除redis中数据
        for (String likeCommentKey : likeCommentKeys){
            LikeComment likeComment = (LikeComment) redisTemplate.opsForValue().get(likeCommentKey);
            likeComment = likeCommentRepository.save(likeComment);
            // 注意redis中的数据没有主键，必须存一次数据库有了主键后再存redis
            redisTemplate.opsForValue().set(likeCommentKey, likeComment, redisTemplate.getExpire(likeCommentKey), TimeUnit.SECONDS);
//            redisTemplate.delete(likeCommentKey);
        }
        return;
    }
}
