package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    USER(0,"用户"),
    ADMIN(1,"管理员")
    ;

    private Integer code;

    private String message;

    RoleEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
