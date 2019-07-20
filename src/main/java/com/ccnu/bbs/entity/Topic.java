package com.ccnu.bbs.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
public class Topic {

    /** 版块id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;
    /** 版块名称. */
    private String topicName;
    /** 版块编号. */
    private Integer topicType;
    /** 创建日期. */
    @CreatedDate
    private Date topicCreateTime;
    /** 更新日期. */
    @LastModifiedDate
    private Date topicUpdateTime;
}
