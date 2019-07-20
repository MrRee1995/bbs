package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface ArticleRepository extends JpaRepository<Article, String> {

    // 分页查看所有帖子
    @Query("select a from Article a where a.articleIsDelete = 0 order by a.articleHotNum desc")
    Page<Article> findAll(Pageable pageable);

    // 分版块查看帖子按热度排序
    @Query("select a from Article a where a.articleIsDelete = 0 and a.articleTopicType = ?1 order by a.articleHotNum desc")
    Page<Article> findAllByTopicHot(Integer zoneId, Pageable pageable);

    @Query("select a from Article a where a.articleIsDelete = 0 and a.articleTopicType = ?1 order by a.articleCreateTime desc")
    Page<Article> findAllByTopicTime(Integer zoneId, Pageable pageable);

    // 查看用户所发帖子
    @Query("select a from Article a where a.articleUserId = ?1 and a.articleIsDelete = 0 " +
            "order by a.articleCreateTime desc")
    Page<Article> findUserArticle(String userId, Pageable pageable);

    // 查看被某位用户收藏的帖子
    @Query("select a from Article a, com.ccnu.bbs.entity.Collect c where " +
            "a.articleId = c.collectArticleId and c.collectUserId = ?1 " +
            "and c.isCollect = 1 and a.articleIsDelete = 0 order by a.articleCreateTime desc ")
    Page<Article> findUserCollect(String userId, Pageable pageable);

    // 查看被某位用户点赞的帖子
    @Query("select a from Article a, com.ccnu.bbs.entity.LikeArticle l where " +
            "a.articleId = l.likeArticleId and l.likeUserId = ?1 " +
            "and l.isLike = 1 and a.articleIsDelete = 0 order by a.articleCreateTime desc ")
    Page<Article> findUserLike(String userId, Pageable pageable);

    @Query("select a from Article a where a.articleId = ?1")
    Article findArticle(String articleId);
}
