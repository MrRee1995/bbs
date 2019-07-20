package com.ccnu.bbs.VO;

import lombok.Data;

@Data
public class ReplyVO {

    /** 回复id. */
    private String replyId;
    /** 用户id. */
    private String usereId;
    /** 用户昵称. */
    private String userName;
    /** 用户头像. */
    private String userImg;
    /** 用户身份. */
    private Integer userRole;
    /** 是否是本人. */
    private Boolean isOneself;
    /** 回复内容. */
    private String replyContent;
    /** 回复时间. */
    private String replyTime;
}
