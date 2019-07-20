package com.ccnu.bbs.service;

import com.ccnu.bbs.entity.LikeArticle;
import com.ccnu.bbs.entity.LikeComment;
import com.ccnu.bbs.forms.LikeArticleForm;
import com.ccnu.bbs.forms.LikeCommentForm;

public interface LikeService {

    /** 查看帖子是否被点赞. */
    Boolean isArticleLike(String articleId, String userId);

    /** 查看评论是否被点赞. */
    Boolean isCommentLike(String commentId, String userId);

    /** 帖子点赞更新. */
    LikeArticle updateLikeArticle(LikeArticleForm likeArticleForm, String userId);

    /** 评论点赞更新. */
    LikeComment updateLikeComment(LikeCommentForm likeCommentForm, String userId);

    /** 从redis更新帖子点赞到数据库. */
    void updateLikeArticleDatabase();

    /** 从redis更新评论点赞到数据库. */
    void updateLikeCommentDatabase();
}
