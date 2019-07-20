package com.ccnu.bbs.VO;

import lombok.Data;

import java.util.List;

@Data
public class ArticleVO {

    /** 帖子id. */
    private String articleId;
    /** 帖子所属版块. */
    private Integer articleTopicType;
    /** 用户id. */
    private String userId;
    /** 用户昵称. */
    private String userName;
    /** 用户头像. */
    private String userImg;
    /** 用户身份. */
    private Integer userRole;
    /** 是否是本人. */
    private Boolean isOneself;
    /** 帖子标题. */
    private String articleTitle;
    /** 帖子内容. */
    private String articleContent;
    /** 帖子浏览次数. */
    private Integer articleViewNum;
    /** 帖子评论次数. */
    private Integer articleCommentNum;
    /** 帖子热度指数. */
    private Double articleHotNum;
    /** 帖子点赞次数. */
    private Integer articleLikeNum;
    /** 是否被当前用户点赞过. */
    private Boolean isLike;
    /** 是否被当前用户收藏过. */
    private Boolean isCollect;
    /** 帖子是否被删除. */
    private Boolean isDelete;
    /** 帖子发表时间. */
    private String articleCreateTime;
    /** 帖子关键词. */
    private List<String> keywords;
    /** 帖子图片. */
    private List<String> articleImages;
}
