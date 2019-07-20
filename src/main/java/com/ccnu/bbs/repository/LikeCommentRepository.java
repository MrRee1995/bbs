package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface LikeCommentRepository extends JpaRepository<LikeComment, BigInteger> {

    @Query("select l from LikeComment l where l.likeCommentId = ?1 and l.likeUserId = ?2")
    LikeComment findLikeComment(String commentId, String userId);
}
