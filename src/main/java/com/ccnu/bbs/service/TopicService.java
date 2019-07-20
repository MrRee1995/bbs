package com.ccnu.bbs.service;

import com.ccnu.bbs.VO.TopicVO;

import java.util.List;

public interface TopicService {

    /** 获取所有版块. */
    List<TopicVO> allTopic();

    /** 获取非热门版块. */
    List<TopicVO> listTopic();
}
