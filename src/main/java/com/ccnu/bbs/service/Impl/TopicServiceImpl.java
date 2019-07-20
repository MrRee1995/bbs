package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.VO.TopicVO;
import com.ccnu.bbs.entity.Topic;
import com.ccnu.bbs.repository.SlideshowRepository;
import com.ccnu.bbs.repository.TopicRepository;
import com.ccnu.bbs.service.TopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SlideshowRepository slideshowRepository;

    @Override
    /**
     * 获取所有版块(包括热门)
     */
    @Cacheable(value = "allTopics")
    public List<TopicVO> allTopic() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream().map(e -> topic2topicVO(e)).collect(Collectors.toList());
    }

    @Override
    /**
     * 获取可选择版块
     */
    @Cacheable(value = "listTopics")
    public List<TopicVO> listTopic(){
        List<Topic> topics = topicRepository.topicList();
        return topics.stream().map(e -> topic2topicVO(e)).collect(Collectors.toList());
    }


    private TopicVO topic2topicVO(Topic topic){
        TopicVO topicVO = new TopicVO();
        BeanUtils.copyProperties(topic, topicVO);
        topicVO.setImgUrls(slideshowRepository.findTopicImg(topic.getTopicType()));
        return topicVO;
    }
}
