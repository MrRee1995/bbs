package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Attention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AttentionRepository extends JpaRepository<Attention, Integer> {

    @Query("select a from Attention a where a.attentionUserId = ?1 and a.attentionFollowerId = ?2")
    Attention findAttention(String attentionUserId, String attentionFollowerId);
}
