package com.ccnu.bbs.service;

import com.ccnu.bbs.entity.Attention;
import com.ccnu.bbs.forms.AttentionForm;

public interface AttentionService {

    /** 查看用户是否被关注. */
    Integer isUserAttention(String attentionUserId, String attentionFollowerId);

    /** 帖子收藏更新. */
    Attention updateAttention(AttentionForm attentionForm, String attentionFollowerId);

    /** 从redis更新数据库. */
    void updateAttentionDatabase();
}
