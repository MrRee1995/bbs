package com.ccnu.bbs.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Comment implements Serializable {

    /** 评论id. */
    @Id
    private String commentId;
    /** 评论文章id. */
    private String commentArticleId;
    /** 评论用户id. */
    private String commentUserId;
    /** 评论内容. */
    private String commentContent;
    /** 评论点赞数. */
    private Integer commentLikeNum = 0;
    @CreatedDate
    /** 评论时间. */
    private Date commentTime;
}
