package com.ccnu.bbs.VO;

import lombok.Data;

@Data
public class UserInfoVO extends UserBasicInfoVO{

    /** 用户性别. */
    private Integer userGender;
    /** 用户所在学院. */
    private String userDepartment;
    /** 用户粉丝数. */
    private Integer userFansNum = 0;
    /** 用户关注数. */
    private Integer userAttentionNum = 0;
    /** 用户发帖数. */
    private Integer userArticleNum = 0;
    /* 用户是否被关注(0代表未关注,1代表已关注,2代表是本人). */
    private Integer isAttention;
}
