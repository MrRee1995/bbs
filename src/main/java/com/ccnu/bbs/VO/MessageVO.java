package com.ccnu.bbs.VO;

import lombok.Data;

import java.math.BigInteger;

@Data
public class MessageVO {

    /** 消息id. */
    private BigInteger messageId;
    /** 消息对应帖子id. */
    private String articleId;
    /** 消息对应评论id. */
    private String commentId;
    /** 消息发送者id. */
    private String senderUserId;
    /** 消息发送者昵称. */
    private String senderUserName;
    /** 消息发送者头像. */
    private String senderUserImg;
    /** 消息回复的内容. */
    private String repliedContent;
    /** 消息内容. */
    private String messageContent;
    /** 消息创建时间. */
    private String messageTime;
    /** 消息是否已读. */
    private Integer isRead;
}
