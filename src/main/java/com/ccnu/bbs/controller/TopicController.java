package com.ccnu.bbs.controller;

import com.ccnu.bbs.VO.ResultVO;
import com.ccnu.bbs.service.Impl.TopicServiceImpl;
import com.ccnu.bbs.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicServiceImpl topicService;

    @GetMapping("/all")
    public ResultVO all(){
        return ResultVOUtil.success(topicService.allTopic());
    }

    @GetMapping("/list")
    public ResultVO list(){
        return ResultVOUtil.success(topicService.listTopic());
    }
}
