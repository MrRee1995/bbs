package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Collect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CollectRepository extends JpaRepository<Collect, Integer> {

    @Query("select c from Collect c where c.collectArticleId = ?1 and c.collectUserId = ?2")
    Collect findCollect(String articleId, String userId);
}
