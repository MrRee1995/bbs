package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserModifyForm {

    /** 用户情感状态. */
    @NotNull
    private Integer userEmotion;
    /** 用户个性签名. */
    private String userShow;
}
