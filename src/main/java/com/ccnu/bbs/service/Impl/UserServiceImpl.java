package com.ccnu.bbs.service.Impl;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.ccnu.bbs.VO.UserBasicInfoVO;
import com.ccnu.bbs.VO.UserInfoVO;
import com.ccnu.bbs.converter.User2UserBasicInfoVO;
import com.ccnu.bbs.entity.Department;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.enums.RoleEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.UserModifyForm;
import com.ccnu.bbs.repository.DepartmentRepository;
import com.ccnu.bbs.repository.UserRepository;
import com.ccnu.bbs.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    /**
     * 查找用户
     */
    public User findUser(String userId) {
        User user = getUser(userId);
        if (user != null){
            redisTemplate.opsForValue().set("User::" + userId, user, 1, TimeUnit.HOURS);
        }
        return user;
    }

    @Override
    /**
     * 存储用户
     */
    public User saveUser(User user){
        redisTemplate.delete("User::" + user.getUserId());
        return userRepository.save(user);
    }

    @Override
    /**
     * 创建用户
     */
    @Transactional
    public User createUser(String userId) {
        User user = new User();
        user.setUserRoleType(RoleEnum.USER.getCode());
        user.setUserId(userId);
        user = userRepository.save(user);
        return user;
    }

    @Override
    /**
     * 从微信更新用户信息
     */
    @Transactional
    public User updateUser(WxMaUserInfo userInfo) {
        String userId = userInfo.getOpenId();
        User user = getUser(userId);
        if (user == null){
            throw new BBSException(ResultEnum.USER_NOT_EXIT);
        }
        user.setUserName(userInfo.getNickName());
        user.setUserGender(Integer.valueOf(userInfo.getGender()));
        user.setUserCity(userInfo.getCity());
        user.setUserProvince(userInfo.getProvince());
        user.setUserCountry(userInfo.getCountry());
        user.setUserImg(userInfo.getAvatarUrl());
        return saveUser(user);
    }

    @Override
    /**
     * 获取用户基本信息
     */
    public UserBasicInfoVO getUserBasicInfo(String userId){
        User user = findUser(userId);
        return User2UserBasicInfoVO.convert(user);
    }

    @Override
    /**
     * 获取用户详细信息
     */
    public UserInfoVO getUserInfo(String userId){
        User user = findUser(userId);
        UserInfoVO userInfoVO = new UserInfoVO();
        Department department = departmentRepository.findUserDepartment(user.getUserDepartment());
        BeanUtils.copyProperties(user, userInfoVO);
        if (department != null){
            userInfoVO.setUserDepartment(department.getDepartmentName());
        }
        return userInfoVO;
    }

    @Override
    /**
     * 获取用户关注的人
     */
    public Page<UserBasicInfoVO> getUserAttentions(String userId, Pageable pageable){
        Page<User> users = userRepository.findAttention(userId, pageable);
        List<UserBasicInfoVO> userList = users.stream()
                .map(e -> User2UserBasicInfoVO.convert(e)).collect(Collectors.toList());
        return new PageImpl(userList, pageable, users.getTotalElements());
    }

    @Override
    /**
     * 获取用户的粉丝
     */
    public Page<UserBasicInfoVO> getUserFollowers(String userId, Pageable pageable){
        Page<User> users = userRepository.findFollower(userId, pageable);
        List<UserBasicInfoVO> userList = users.stream()
                .map(e -> User2UserBasicInfoVO.convert(e)).collect(Collectors.toList());
        return new PageImpl(userList, pageable, users.getTotalElements());
    }

    @Override
    /**
     * 修改用户信息
     */
    @Transactional
    public User modifyUserInfo(String userId, UserModifyForm userModifyForm){
        User user = getUser(userId);
        if (user == null){
            throw new BBSException(ResultEnum.USER_NOT_EXIT);
        }
        user.setUserEmotion(userModifyForm.getUserEmotion());
        user.setUserShow(userModifyForm.getUserShow());
        return saveUser(user);
    }

    public User getUser(String userId){
        User user;
        if (redisTemplate.hasKey("User::" + userId)){
            user = (User)redisTemplate.opsForValue().get("User::" + userId);
        }
        else {
            user = userRepository.findByUserId(userId);
        }
        return user;
    }
}
