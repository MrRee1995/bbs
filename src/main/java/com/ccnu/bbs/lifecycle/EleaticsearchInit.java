package com.ccnu.bbs.lifecycle;

import com.ccnu.bbs.entity.Article;
import com.ccnu.bbs.repository.ArticleRepository;
import com.ccnu.bbs.searchRepository.ArticleSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class EleaticsearchInit implements ApplicationRunner {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleSearchRepository articleSearchRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run(ApplicationArguments args) {
        log.info("正在更新Elasticsearch数据-------- {}", sdf.format(new Date()));
        List<Article> articles = articleRepository.findAll();
        if (articles != null && !articles.isEmpty()){
            articleSearchRepository.saveAll(articles);
        }
        log.info("更新Elasticsearch数据完成-------- {}", sdf.format(new Date()));
    }
}
