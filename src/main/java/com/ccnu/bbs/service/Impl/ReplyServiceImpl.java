package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.VO.ReplyVO;
import com.ccnu.bbs.converter.Date2StringConverter;
import com.ccnu.bbs.entity.*;
import com.ccnu.bbs.enums.MessageEnum;
import com.ccnu.bbs.exception.BBSException;
import com.ccnu.bbs.forms.ReplyForm;
import com.ccnu.bbs.repository.ReplyRepository;
import com.ccnu.bbs.service.ReplyService;
import com.ccnu.bbs.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MessageServiceImpl messageService;

    @Override
    /**
     * 查找评论的回复列表
     */
    public Page<ReplyVO> commentReply(String userId, String commentId, Pageable pageable) {
        // 1.根据评论id查询出回复
        Page<Reply> replies = replyRepository.findCommentReply(commentId, pageable);
        List<ReplyVO> replyVOList = new ArrayList();
        // 2.将用户信息放入评论中
        for (Reply reply : replies){
            ReplyVO replyVO = new ReplyVO();
            // 获得回复信息
            BeanUtils.copyProperties(reply, replyVO);
            // 获得回复作者信息
            User user = userService.findUser(reply.getReplyUserId());
            BeanUtils.copyProperties(user, replyVO);
            // 获得作者身份
            replyVO.setUserRole(user.getUserRoleType());
            // 查看是否是当前用户所发回复
            replyVO.setIsOneself(userId.equals(user.getUserId()) ? true : false);
            // 获得时间
            replyVO.setReplyTime(Date2StringConverter.convert(reply.getReplyTime()));
            replyVOList.add(replyVO);
        }
        return new PageImpl(replyVOList, pageable, replies.getTotalElements());
    }

    @Override
    /**
     * 创建回复
     */
    @Transactional
    public Reply createReply(ReplyForm replyForm, String userId) throws BBSException {
        Reply reply = new Reply();
        String commentId = replyForm.getCommentId();
        String replyId = replyForm.getReplyId();
        BeanUtils.copyProperties(replyForm, reply);
        // 1.设置主键
        reply.setReplyId(KeyUtil.genUniqueKey());
        // 2.设置评论id
        reply.setReplyCommentId(commentId);
        // 3.设置回复者
        reply.setReplyUserId(userId);
        // 4.获取评论
        Comment comment = commentService.getComment(commentId);
        // 5.发送消息
        // 如果有replyId，说明回复的是楼中楼的回复
        if (replyId != null){
            // 所以找到该回复
            Reply beReplied = replyRepository.findReply(replyId);
            // 如果数据库中有该reply并且reply的用户不是本人，就创建新消息
            if (beReplied != null && !userId.equals(beReplied.getReplyUserId())){
                Message message = new Message();
                // 找到被回复者所在帖子,将帖子id存入
                message.setArticleId(comment.getCommentArticleId());
                // 存入其他信息
                message.setCommentId(comment.getCommentId());
                message.setMessageType(MessageEnum.REPLY_MESSAGE.getCode());
                message.setReceiverUserId(beReplied.getReplyUserId());
                message.setSenderUserId(userId);
                message.setRepliedContent(beReplied.getReplyContent());
                message.setMessageContent(reply.getReplyContent());
                messageService.createMessage(message);
            }
        }
        // 如果没有replyId，说明回复的是层主
        else {
            // 如果被回复的层主不是本人，则创建新消息
            if (!userId.equals(comment.getCommentUserId())){
                Message message = new Message();
                // 找到被评论者所在帖子,将帖子id存入
                message.setArticleId(comment.getCommentArticleId());
                // 存入其他信息
                message.setCommentId(comment.getCommentId());
                message.setMessageType(MessageEnum.REPLY_MESSAGE.getCode());
                message.setReceiverUserId(comment.getCommentUserId());
                message.setSenderUserId(userId);
                message.setRepliedContent(comment.getCommentContent());
                message.setMessageContent(reply.getReplyContent());
                messageService.createMessage(message);
            }
        }
        // 保存回复
        return replyRepository.save(reply);
    }
}
