package com.ccnu.bbs.VO;

import lombok.Data;

import java.util.List;

@Data
public class CommentVO {

    /** 评论id. */
    private String commentId;
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
    /** 评论内容. */
    private String commentContent;
    /** 评论点赞数. */
    private Integer commentLikeNum;
    /** 是否被当前用户点赞过. */
    private Boolean isLike;
    /** 帖子是否被删除. */
    private Boolean isArticleDelete;
    /** 评论时间. */
    private String commentTime;
    /** 回复列表. */
    private List<ReplyVO> replies;
}
