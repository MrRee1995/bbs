package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {

    LIKE_MESSAGE(0,"点赞消息"),
    REPLY_MESSAGE(1,"回复消息")
    ;

    private Integer code;

    private String message;

    MessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
