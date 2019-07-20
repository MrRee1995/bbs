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
public class Reply implements Serializable {

    /** 回复id. */
    @Id
    private String replyId;
    /** 被回复评论id. */
    private String replyCommentId;
    /** 回复用户id. */
    private String replyUserId;
    /** 回复内容. */
    private String replyContent;
    @CreatedDate
    /** 回复时间. */
    private Date replyTime;
}
