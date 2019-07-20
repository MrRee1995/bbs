package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum EmotionEnum {
    UNKNOWN(0, "未知"),
    SINGLE(1, "单身中"),
    LOVESTRUCK(2, "热恋中"),
    LOVELORN(3, "失恋中")
    ;
    private Integer code;

    private String message;

    EmotionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
