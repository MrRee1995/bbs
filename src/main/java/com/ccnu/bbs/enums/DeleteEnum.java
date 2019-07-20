package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum DeleteEnum {

    NOT_DELETE(0,"未删除"),
    DELETE(1,"已删除")
    ;

    private Integer code;

    private String message;

    DeleteEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
