package com.ccnu.bbs.service;

import com.ccnu.bbs.VO.ReplyVO;
import com.ccnu.bbs.entity.Reply;
import com.ccnu.bbs.forms.ReplyForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReplyService {

    /** 查找评论的回复. */
    Page<ReplyVO> commentReply(String userId, String commentId, Pageable pageable);

    /** 创建回复. */
    Reply createReply(ReplyForm replyForm, String userId);
}
