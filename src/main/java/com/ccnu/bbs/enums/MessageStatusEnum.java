package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum MessageStatusEnum {

    NOT_READ(0,"未读消息"),
    READ(1,"已读消息")
    ;

    private Integer code;

    private String message;

    MessageStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
