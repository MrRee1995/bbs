package com.ccnu.bbs.service;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.ccnu.bbs.VO.UserBasicInfoVO;
import com.ccnu.bbs.VO.UserInfoVO;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.forms.UserModifyForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /** 查询用户. */
    User findUser(String userId);
    /** 存储用户. */
    User saveUser(User user);
    /** 创建新用户. */
    User createUser(String userId);
    /** 获得用户基本信息. */
    UserBasicInfoVO getUserBasicInfo(String userId);
    /** 获得用户信息. */
    UserInfoVO getUserInfo(String userId);
    /** 更新用户信息. */
    User updateUser(WxMaUserInfo userInfo);
    /** 修改用户信息. */
    User modifyUserInfo(String userId, UserModifyForm userModifyForm);
    /** 获取用户关注的人. */
    Page<UserBasicInfoVO> getUserAttentions(String userId, Pageable pageable);
    /** 获取用户的粉丝. */
    Page<UserBasicInfoVO> getUserFollowers(String userId, Pageable pageable);
}
