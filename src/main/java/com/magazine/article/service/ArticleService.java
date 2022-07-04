package com.magazine.article.service;

import com.magazine.article.model.Article;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * Retrieves articles on the page with specified number and size
     *
     * @param page page number on which articles are to be retrieved
     * @param size number of articles per page
     * @return page of articles
     */
    Page<Article> getArticles(int page, int size);

    /**
     * Counts number of articles per each date from the specified period of time
     *
     * @param startDate start date of the period
     * @param endDate   end date of the period
     * @return map of dates and respective numbers of articles
     */
    Map<LocalDate, Long> countArticlesPerDate(LocalDateTime startDate, LocalDateTime endDate);
}
