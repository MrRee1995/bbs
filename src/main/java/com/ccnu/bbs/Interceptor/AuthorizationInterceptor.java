package com.ccnu.bbs.Interceptor;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.ccnu.bbs.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1.获得请求头中的sessionId
        String sessionId = request.getParameter("sessionId");
        log.info("--------sessionId:{}--------", sessionId);
        if (sessionId == null || !redisTemplate.hasKey("sessionId::" + sessionId)){
            log.error(ResultEnum.SESSION_ID_NULL.getMessage());
            return false;
        }
        // 2.得到用户信息
        WxMaJscode2SessionResult session = (WxMaJscode2SessionResult) redisTemplate.opsForValue().get("sessionId::" + sessionId);
        String userId = session.getOpenid();
        request.setAttribute("userId", userId);
        return true;
    }
}
