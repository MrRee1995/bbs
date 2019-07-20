package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum UserGenderEnum implements CodeEnum {

    UNKNOWN(0,"未知"),
    MAN(1,"男"),
    WOMAN(2, "女")
    ;

    private Integer code;

    private String message;

    UserGenderEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
