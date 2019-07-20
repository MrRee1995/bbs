package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CommentForm {

    /** 评论文章id. */
    @NotEmpty
    private String articleId;
    /** 评论内容. */
    @NotEmpty
    private String commentContent;
}
