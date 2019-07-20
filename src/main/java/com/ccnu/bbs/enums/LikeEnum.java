package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum LikeEnum {

    NOT_LIKE(0,"未点赞"),
    LIKE(1,"已点赞")
    ;

    private Integer code;

    private String message;

    LikeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
