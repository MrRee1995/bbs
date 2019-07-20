package com.ccnu.bbs.VO;

import lombok.Data;

import java.util.List;

@Data
public class TopicVO {

    /** 版块名称. */
    private String topicName;
    /** 版块类型. */
    private Integer topicType;
    /** 轮播图url. */
    private List<String> imgUrls;
}
