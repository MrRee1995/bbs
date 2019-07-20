package com.ccnu.bbs.task;

import com.ccnu.bbs.service.Impl.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class BBSTask extends QuartzJobBean {

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private CollectServiceImpl collectService;

    @Autowired
    private AttentionServiceImpl attentionService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        log.info("BBSTask-------- {}", sdf.format(new Date()));
        articleService.updateArticleDatabase();
        commentService.updateCommentDatabase();
        likeService.updateLikeArticleDatabase();
        likeService.updateLikeCommentDatabase();
        collectService.updateCollectDatabase();
        attentionService.updateAttentionDatabase();
    }
}
