package com.ccnu.bbs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Slideshow {

    /** 轮播图id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer slideshowId;
    /** 版块类型. */
    private Integer topicType;
    /** 图片url. */
    private String imgUrl;
}
