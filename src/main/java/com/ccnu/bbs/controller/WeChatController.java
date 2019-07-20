package com.ccnu.bbs.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.ccnu.bbs.VO.ResultVO;
import com.ccnu.bbs.entity.User;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.forms.UserInfoForm;
import com.ccnu.bbs.service.Impl.UserServiceImpl;
import com.ccnu.bbs.utils.KeyUtil;
import com.ccnu.bbs.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/wechat")
public class WeChatController {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/check")
    public ResultVO WeChatCheckSession(@RequestParam String sessionId){
        if (redisTemplate.hasKey("sessionId::" + sessionId)){
            return ResultVOUtil.success(true);
        }
        else{
            return ResultVOUtil.success(false);
        }
    }

    @GetMapping("/login")
    public ResultVO WeChatLogin(@RequestParam String code){
        String sessionId;
        try{
            // 0.如果code为空，返回错误信息
            if (code==null||code.isEmpty()){
                return ResultVOUtil.error(ResultEnum.CODE_ERROR.getCode(), ResultEnum.CODE_ERROR.getMessage());
            }
            // 1.向微信服务器获取openid和sessionKey
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            if (session==null){
                return ResultVOUtil.error(ResultEnum.SESSION_ERROR.getCode(), ResultEnum.SESSION_ERROR.getMessage());
            }
            String sessionKey = session.getSessionKey();
            String openId = session.getOpenid();
            // 3.根据openid查询用户是否存在
            User user = userService.findUser(session.getOpenid());
            // 4.若用户不存在则创建用户
            if (user == null){
                userService.createUser(session.getOpenid());
            }
            // 5.查看redis中是否有登录信息
            if (redisTemplate.hasKey("openId::" + openId)){
                redisTemplate.delete(redisTemplate.opsForValue().get("openId::" + openId));
            }
            // 6.生成加密的sessionId;
            sessionId = KeyUtil.getSessionId(sessionKey+openId+System.currentTimeMillis());
            // 7.存入redis中
            redisTemplate.opsForValue().set("sessionId::" + sessionId, session,30, TimeUnit.DAYS);
            redisTemplate.opsForValue().set("openId::" + openId, "sessionId::" + sessionId, 30, TimeUnit.DAYS);
        }catch (WxErrorException e){
            log.error(e.getMessage(), e);
            return ResultVOUtil.error(e.getError().getErrorCode(), e.getError().getErrorMsg());
        }
        // 8.返回第三方sessionId交由客户端保存
        HashMap<String, String> map = new HashMap();
        map.put("sessionId", sessionId);
        return ResultVOUtil.success(map);
    }

    @PostMapping("/info")
    public ResultVO<String> WeChatInfo(@RequestParam String sessionId,
                                       @RequestBody UserInfoForm userInfoForm){
        String rawData = userInfoForm.getRawData();
        String signature = userInfoForm.getSignature();
        String encryptedData = userInfoForm.getEncryptedData();
        String iv = userInfoForm.getIv();
        // 1.查看是否有sessionId信息
        if (!redisTemplate.hasKey("sessionId::" + sessionId)){
            return ResultVOUtil.error(ResultEnum.SESSION_ID_NULL.getCode(), ResultEnum.SESSION_ID_NULL.getMessage());
        }
        // 2.从sessionId中取出sessionKey
        WxMaJscode2SessionResult session = (WxMaJscode2SessionResult) redisTemplate.opsForValue().get("sessionId::" + sessionId);
        String sessionKey = session.getSessionKey();
        // 3.校验用户信息
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return ResultVOUtil.error(ResultEnum.USER_INFO_ERROR.getCode(), ResultEnum.USER_INFO_ERROR.getMessage());
        }
        // 4.解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        // 5.更新用户信息
        User user = userService.updateUser(userInfo);
        return ResultVOUtil.success(user);
    }
}
