package com.ccnu.bbs.VO;

import lombok.Data;

@Data
public class UserBasicInfoVO {

    /** 用户id. */
    private String userId;
    /** 用户昵称. */
    private String userName;
    /** 用户情感状态. */
    private Integer userEmotion;
    /** 用户个性签名. */
    private String userShow;
    /** 用户头像. */
    private String userImg;
    /** 用户粉丝数. */
    private Integer userFansNum;
    /** 用户关注数. */
    private Integer userAttentionNum;
    /** 用户发帖数. */
    private Integer userArticleNum;
}
