package com.ccnu.bbs.searchRepository;

import com.ccnu.bbs.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleSearchRepository extends ElasticsearchRepository<Article, String> {
}
