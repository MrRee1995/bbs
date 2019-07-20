package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    // 查看某个帖子的评论(按照点赞数降序排列)
    @Query("select c from Comment c where c.commentArticleId = ?1 and c.commentLikeNum > 10 order by c.commentLikeNum desc")
    List<Comment> findArticleCommentByLike(String articleId);

    // 查看某个帖子的评论(按评论时间升序排列)
    @Query("select c from Comment c where c.commentArticleId = ?1 order by c.commentTime asc")
    Page<Comment> findArticleCommentByTime(String articleId, Pageable pageable);

    // 查看某个用户的评论((按照评论时间降序排列)
    @Query("select c from Comment c where c.commentUserId = ?1 order by c.commentTime desc")
    Page<Comment> findUserComment(String userId, Pageable pageable);

    // 查看被某个用户点赞的评论
    @Query("select c from Comment c, com.ccnu.bbs.entity.LikeComment l where " +
            "c.commentId = l.likeCommentId and l.likeUserId = ?1")
    Page<Comment> findUserLike(String userId, Pageable pageable);

    @Query("select c from Comment c where c.commentId = ?1")
    Comment findComment(String commentId);
}
