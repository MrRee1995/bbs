package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum CollectEnum {

    NOT_COLLECT(0,"未收藏"),
    COLLECT(1,"已收藏")
    ;

    private Integer code;

    private String message;

    CollectEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
