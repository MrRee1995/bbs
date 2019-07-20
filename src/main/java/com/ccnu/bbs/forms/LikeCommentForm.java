package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LikeCommentForm {

    /** 被点赞评论id. */
    @NotEmpty
    private String likeCommentId;
    /** 是否点赞. */
    @NotNull
    private Integer isLike;
}
