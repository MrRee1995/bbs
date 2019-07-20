package com.ccnu.bbs.enums;

import lombok.Getter;

@Getter
public enum  ResultEnum {

    SUCCESS(0, "成功"),
    CODE_ERROR(1, "未获取到code"),
    SESSION_ERROR(2, "获取sessionKey和openid失败"),
    SESSION_ID_NULL(3, "sessionId不存在"),
    USER_INFO_ERROR(4, "用户信息校验失败"),
    PARAM_ERROR(5, "表单参数不正确"),
    UPLOAD_ERROR(6, "文件上传出错"),
    ARTICLE_ID_ERROR(7, "帖子id不能为空"),
    COMMENT_ID_ERROR(8, "评论id不能为空"),
    USER_NOT_EXIT(9, "用户不存在"),
    ARTICLE_NOT_EXIT(10, "帖子不存在"),
    COMMENT_NOT_EXIT(11, "评论不存在"),
    REPLY_NOT_EXIT(12, "回复不存在"),
    RISKY_CONTENT(13, "怀疑你在搞黄色哦"),
    IMG_URL_EMPTY(14, "图片Url为空")
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
