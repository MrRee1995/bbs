package com.ccnu.bbs.converter;

import com.ccnu.bbs.VO.UserBasicInfoVO;
import com.ccnu.bbs.entity.User;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class User2UserBasicInfoVO {

    public static UserBasicInfoVO convert(User user){
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        BeanUtils.copyProperties(user, userBasicInfoVO);
        return userBasicInfoVO;
    }

    public static List<UserBasicInfoVO> convert(List<User> topics){
        return topics.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
