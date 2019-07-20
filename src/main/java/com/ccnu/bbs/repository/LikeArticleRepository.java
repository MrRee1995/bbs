package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.LikeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface LikeArticleRepository extends JpaRepository<LikeArticle, BigInteger> {

    @Query("select l from LikeArticle l where l.likeArticleId = ?1 and l.likeUserId = ?2")
    LikeArticle findLikeArticle(String articleId, String userId);
}
