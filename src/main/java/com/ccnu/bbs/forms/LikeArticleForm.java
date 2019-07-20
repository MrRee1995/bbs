package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LikeArticleForm {

    /** 被点赞帖子id. */
    @NotEmpty
    private String likeArticleId;
    /** 是否点赞. */
    @NotNull
    private Integer isLike;
}
