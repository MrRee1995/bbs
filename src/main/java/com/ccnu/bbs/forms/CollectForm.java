package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CollectForm {

    /** 收藏帖子id. */
    @NotEmpty
    private String collectArticleId;
    /** 是否收藏. */
    @NotNull
    private Integer isCollect;
}
