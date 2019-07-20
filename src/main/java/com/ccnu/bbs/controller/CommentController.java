package com.ccnu.bbs.controller;

import cn.binarywang.wx.miniapp.api.WxMaSecCheckService;
import com.ccnu.bbs.VO.CommentVO;
import com.ccnu.bbs.VO.ResultVO;
import com.ccnu.bbs.entity.Comment;
import com.ccnu.bbs.enums.ResultEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.CommentForm;
import com.ccnu.bbs.forms.LikeCommentForm;
import com.ccnu.bbs.service.Impl.CommentServiceImpl;
import com.ccnu.bbs.service.Impl.LikeServiceImpl;
import com.ccnu.bbs.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private WxMaSecCheckService wxMaSecCheckService;

    /**
     * 评论列表
     * @param userId
     * @param articleId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResultVO list(@RequestAttribute String userId,
                         @RequestParam String articleId,
                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size){
        // 1.校验帖子id
        if (articleId == null||articleId.isEmpty()){
            return ResultVOUtil.error(ResultEnum.ARTICLE_ID_ERROR.getCode(), ResultEnum.ARTICLE_ID_ERROR.getMessage());
        }
        Page<CommentVO> commentVOList = commentService.articleComment(userId, articleId, PageRequest.of(page - 1, size));
        return ResultVOUtil.success(commentVOList);
    }

    /**
     * 热评
     * @param articleId
     * @return
     */
    @GetMapping("/hot")
    public ResultVO hot(@RequestAttribute String userId,
                        @RequestParam String articleId){
        // 1.校验帖子id
        if (articleId == null||articleId.isEmpty()){
            return ResultVOUtil.error(ResultEnum.ARTICLE_ID_ERROR.getCode(), ResultEnum.ARTICLE_ID_ERROR.getMessage());
        }
        List<CommentVO> commentVOList = commentService.hotArticleComment(userId, articleId);
        return  ResultVOUtil.success(commentVOList);
    }

    /**
     * 创建评论
     * @param userId
     * @param commentForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/create")
    public ResultVO create(@RequestAttribute String userId,
                           @RequestBody CommentForm commentForm,
                           BindingResult bindingResult){
        // 1.查看表单参数是否有问题
        if (bindingResult.hasErrors()){
            log.error("【发表评论】参数不正确, commentForm={}", commentForm);
            throw new BBSException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        // 2.检测是否含有敏感内容
        Boolean safe = wxMaSecCheckService.checkMessage(commentForm.getCommentContent());
        if (!safe){
            return ResultVOUtil.error(ResultEnum.RISKY_CONTENT.getCode(), ResultEnum.RISKY_CONTENT.getMessage());
        }
        try{
            // 3.将评论保存进数据库中
            Comment comment = commentService.createComment(userId, commentForm);
            // 4.返回评论id
            HashMap<String, String> map = new HashMap();
            map.put("commentId", comment.getCommentId());
            return ResultVOUtil.success(map);
        }
        catch (BBSException e){
            return ResultVOUtil.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 评论内容
     * @param commentId
     * @return
     */
    @GetMapping("/content")
    public ResultVO content(@RequestAttribute String userId,
                            @RequestParam String commentId){
        if (commentId == null){
            return ResultVOUtil.error(ResultEnum.COMMENT_ID_ERROR.getCode(), ResultEnum.COMMENT_ID_ERROR.getMessage());
        }
        try{
            return ResultVOUtil.success(commentService.findComment(commentId, userId));
        }
        catch (BBSException e){
            return ResultVOUtil.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 评论点赞
     * @param userId
     * @param likeCommentForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/like")
    public ResultVO like(@RequestAttribute String userId,
                         @RequestBody LikeCommentForm likeCommentForm,
                         BindingResult bindingResult){
        // 1.查看表单参数是否有问题
        if (bindingResult.hasErrors()){
            log.error("【评论点赞】参数不正确, likeCommentForm={}", likeCommentForm);
            throw new BBSException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        try{
            likeService.updateLikeComment(likeCommentForm, userId);
            return ResultVOUtil.success();
        }
        catch (BBSException e){
            return ResultVOUtil.error(e.getCode(), e.getMessage());
        }
    }
}
