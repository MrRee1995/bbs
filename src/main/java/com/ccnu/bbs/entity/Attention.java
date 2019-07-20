package com.ccnu.bbs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Attention {

    /** 关注id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attentionId;
    /** 被关注人id. */
    private String attentionUserId;
    /** 关注人id. */
    private String attentionFollowerId;
    /** 是否关注. */
    private Integer isAttention;
}
