package com.ccnu.bbs.service;

import com.ccnu.bbs.entity.Collect;
import com.ccnu.bbs.forms.CollectForm;

public interface CollectService {

    /** 查看帖子是否被收藏. */
    Boolean isArticleCollect(String articleId, String userId);

    /** 帖子收藏更新. */
    Collect updateCollectArticle(CollectForm collectForm, String userId);

    /** 从redis更新数据库. */
    void updateCollectDatabase();
}
