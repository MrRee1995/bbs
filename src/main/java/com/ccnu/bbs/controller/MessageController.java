package com.ccnu.bbs.controller;

import com.ccnu.bbs.VO.MessageVO;
import com.ccnu.bbs.VO.ResultVO;
import com.ccnu.bbs.enums.MessageEnum;
import com.ccnu.bbs.service.Impl.MessageServiceImpl;
import com.ccnu.bbs.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;

    /**
     * 点赞新消息提醒
     * @param userId
     * @return
     */
    @GetMapping("/newLike")
    public ResultVO newLike(@RequestAttribute String userId){
        Boolean isRead = messageService.haveMessage(userId, MessageEnum.LIKE_MESSAGE.getCode());
        return ResultVOUtil.success(isRead);
    }

    /**
     * 回复新消息提醒
     * @param userId
     * @return
     */
    @GetMapping("/newReply")
    public ResultVO newReply(@RequestAttribute String userId){
        Boolean isRead = messageService.haveMessage(userId, MessageEnum.REPLY_MESSAGE.getCode());
        return ResultVOUtil.success(isRead);
    }

    /**
     * 点赞消息列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/like")
    public ResultVO like(@RequestAttribute String userId,
                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<MessageVO> messages = messageService.getUserMessage(userId, MessageEnum.LIKE_MESSAGE.getCode(), PageRequest.of(page - 1, size));
        return ResultVOUtil.success(messages);
    }

    /**
     * 回复消息列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/reply")
    public ResultVO reply(@RequestAttribute String userId,
                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size){
        Page<MessageVO> messages = messageService.getUserMessage(userId, MessageEnum.REPLY_MESSAGE.getCode(), PageRequest.of(page - 1, size));
        return ResultVOUtil.success(messages);
    }
}
