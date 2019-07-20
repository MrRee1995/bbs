package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.VO.MessageVO;
import com.ccnu.bbs.converter.Date2StringConverter;
import com.ccnu.bbs.entity.Message;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.enums.MessageStatusEnum;
import com.ccnu.bbs.repository.MessageRepository;
import com.ccnu.bbs.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    /**
     * 得到用户消息
     */
    public Page<MessageVO> getUserMessage(String userId, Integer messageType, Pageable pageable) {
        // 1.按时间降序查找消息
        Page<Message> messages = messageRepository.findMessage(userId, messageType, pageable);
        // 2.对每一个消息进行拼装
        List<MessageVO> messageVOList = messages.stream().map(e -> message2messageVO(e)).collect(Collectors.toList());
        return new PageImpl(messageVOList, pageable, messages.getTotalElements());
    }

    @Override
    /**
     * 查看是否有新消息
     */
    public Boolean haveMessage(String userId, Integer messageType) {
        Integer newMessageCount = messageRepository.haveNewMessage(userId, messageType);
        return newMessageCount > 0 ? true : false;
    }

    @Override
    /**
     * 保存新消息
     */
    @Transactional
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    /**
     * 删除消息
     */
    @Transactional
    public void deleteMessage(BigInteger messageId) {
        messageRepository.deleteByMessageId(messageId);
    }

    /**
     * 消息内容拼装
     * @param message
     * @return
     */
    private MessageVO message2messageVO(Message message){
        // 新建MessageVO对象
        MessageVO messageVO = new MessageVO();
        // 获得Message信息
        BeanUtils.copyProperties(message, messageVO);
        // 获得消息发送者的昵称、头像
        User user = userService.findUser(message.getSenderUserId());
        messageVO.setSenderUserId(user.getUserId());
        messageVO.setSenderUserName(user.getUserName());
        messageVO.setSenderUserImg(user.getUserImg());
        // 获得时间
        messageVO.setMessageTime(Date2StringConverter.convert(message.getMessageTime()));
        // 若为未读消息,将消息设为已读,存入数据库
        if (message.getIsRead().equals(MessageStatusEnum.NOT_READ.getCode())){
            message.setIsRead(MessageStatusEnum.READ.getCode());
            messageRepository.save(message);
        }
        return messageVO;
    }
}
