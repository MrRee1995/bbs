package com.ccnu.bbs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigInteger;

@Data
@Entity
public class LikeArticle {

    /** 点赞id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger likeId;
    /** 被点赞文章id. */
    private String likeArticleId;
    /** 点赞用户id. */
    private String likeUserId;
    /** 是否点赞. */
    private Integer isLike;
}
