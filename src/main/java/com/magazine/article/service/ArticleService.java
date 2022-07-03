package com.magazine.article.service;

import com.magazine.article.model.Article;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Map;

public interface ArticleService {

    /**
     * Creates a new article
     *
     * @param article the article to create
     * @return created article
     */
    Article createArticle(Article article);

    /**
     * Retrieves all articles on certain page with specified page size
     *
     * @param page number of the page
     * @param size size of the page
     * @return page of found articles
     */
    Page<Article> getArticles(int page, int size);

    /**
     * Counts number of articles per each day during the last 7 days
     *
     * @return map of date and respective articles number
     */
    Map<LocalDate, Long> countArticlesPerDateForSevenDays();
}
