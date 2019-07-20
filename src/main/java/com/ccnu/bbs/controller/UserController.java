package com.ccnu.bbs.controller;

import com.ccnu.bbs.VO.ArticleVO;
import com.ccnu.bbs.VO.ResultVO;
import com.ccnu.bbs.VO.UserBasicInfoVO;
import com.ccnu.bbs.VO.UserInfoVO;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.AttentionForm;
import com.ccnu.bbs.forms.UserModifyForm;
import com.ccnu.bbs.service.Impl.ArticleServiceImpl;
import com.ccnu.bbs.service.Impl.AttentionServiceImpl;
import com.ccnu.bbs.service.Impl.UserServiceImpl;
import com.ccnu.bbs.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private AttentionServiceImpl attentionService;

    /**
     * 获取用户基本信息
     * @param userId
     * @return
     */
    @GetMapping("/info")
    public ResultVO<UserBasicInfoVO> info(@RequestAttribute String userId){
        return ResultVOUtil.success(userService.getUserBasicInfo(userId));
    }

    /**
     * 修改用户部分信息
     * @param userId
     * @param userModifyForm
     * @return
     */
    @PostMapping("/modify")
    public ResultVO modify(@RequestAttribute String userId,
                           @RequestBody UserModifyForm userModifyForm){
        userService.modifyUserInfo(userId, userModifyForm);
        return ResultVOUtil.success();
    }

    /**
     * 获得用户详细信息
     * @param userId
     * @param homeUserId
     * @return
     */
    @GetMapping("/home")
    public ResultVO<UserInfoVO> home(@RequestAttribute String userId,
                         @RequestParam String homeUserId){
        UserInfoVO userInfo = userService.getUserInfo(homeUserId);
        if (userId.equals(homeUserId)){
            userInfo.setIsAttention(2);
        }
        else {
            userInfo.setIsAttention(attentionService.isUserAttention(homeUserId, userId));
        }
        return ResultVOUtil.success(userInfo);
    }

    /**
     * 获取用户帖子列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/article")
    public ResultVO<ArticleVO> article(@RequestParam String userId,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size){
        // 查询用户帖子
        Page<ArticleVO> articles = articleService.findUserArticle(userId, PageRequest.of(page - 1, size));
        return ResultVOUtil.success(articles);
    }

    /**
     * 获取用户关注列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/attentions")
    public ResultVO<Page<UserBasicInfoVO>> attentions(@RequestParam String userId,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size){
        return ResultVOUtil.success(userService.getUserAttentions(userId, PageRequest.of(page - 1, size)));
    }

    /**
     * 获取用户粉丝列表
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/followers")
    public ResultVO<Page<UserBasicInfoVO>> followers(@RequestParam String userId,
                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "size", defaultValue = "10") Integer size){
        return ResultVOUtil.success(userService.getUserFollowers(userId, PageRequest.of(page - 1, size)));
    }

    /**
     * 用户关注
     * @param userId
     * @param attentionForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/attention")
    public ResultVO attention(@RequestAttribute String userId,
                              @RequestBody AttentionForm attentionForm,
                              BindingResult bindingResult){
        // 1.查看表单参数是否有问题
        if (bindingResult.hasErrors()){
            log.error("【用户关注】参数不正确, attentionForm={}", attentionForm);
            throw new BBSException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        attentionService.updateAttention(attentionForm, userId);
        return ResultVOUtil.success();
    }
}
