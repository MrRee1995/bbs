package com.ccnu.bbs.entity;

import com.ccnu.bbs.enums.EmotionEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    /** 用户id. */
    @Id
    private String userId;
    /** 用户昵称. */
    private String userName;
    /** 用户身份. */
    private Integer userRoleType;
    /** 用户性别. */
    private Integer userGender;
    /** 用户所在学院. */
    private Integer userDepartment = 0;
    /** 用户经验. */
    private String userEx = "0";
    /** 用户情感状态. */
    private Integer userEmotion = EmotionEnum.UNKNOWN.getCode();
    /** 用户个性签名. */
    private String userShow = "";
    /** 用户头像. */
    private String userImg;
    /** 用户粉丝数. */
    private Integer userFansNum = 0;
    /** 用户关注数. */
    private Integer userAttentionNum = 0;
    /** 用户发帖数. */
    private Integer userArticleNum = 0;
    /** 用户所在城市. */
    private String userCity;
    /** 用户所在省份. */
    private String userProvince;
    /** 用户所在国家. */
    private String userCountry;
    @CreatedDate
    /** 用户注册时间. */
    private Date userTime;
}
