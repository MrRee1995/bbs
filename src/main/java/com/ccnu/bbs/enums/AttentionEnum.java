package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum AttentionEnum {

    NOT_ATTENTION(0,"未关注"),
    ATTENTION(1,"已关注")
    ;

    private Integer code;

    private String message;

    AttentionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
