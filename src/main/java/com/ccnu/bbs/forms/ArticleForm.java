package com.ccnu.bbs.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ArticleForm {

    /** 帖子标题. */
    @NotEmpty
    private String articleTitle;
    /** 帖子版块. */
    @NotNull
    private Integer articleTopicType;
    /** 帖子内容. */
    @NotEmpty
    private String articleContent;
    /** 帖子关键词. */
    private String articleKeywords;
    /** 上传的图片. */
    private List<String> imgUrls;
}
