package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AttentionForm {

    /** 被关注用户id. */
    @NotEmpty
    private String attentionUserId;
    /** 是否关注. */
    @NotNull
    private Integer isAttention;
}
