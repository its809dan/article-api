package com.magazine.article.service.impl;

import com.magazine.article.model.Article;
import com.magazine.article.repository.ArticleRepository;
import com.magazine.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Page<Article> getArticles(int page, int size) {
        return articleRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Map<LocalDate, Long> countArticlesPerDateForSevenDays() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(6);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);
        List<Article> articles = articleRepository.findByPublishingDateGreaterThan(offsetDateTime);
        Map<LocalDate, Long> articlesNumberPerDate = articles.stream()
                                                             .collect(Collectors.groupingBy(article -> article.getPublishingDate().toLocalDate(),
                                                                      Collectors.counting()));
        return fillDatesWithNoArticles(articlesNumberPerDate, offsetDateTime);
    }

    private Map<LocalDate, Long> fillDatesWithNoArticles(Map<LocalDate, Long> articlesNumberPerDate, OffsetDateTime startDate) {
        OffsetDateTime endDate = LocalDateTime.now().atOffset(ZoneOffset.UTC);
        Map<LocalDate, Long> result = new HashMap<>(articlesNumberPerDate);
        for (int i = 0; i < Duration.between(startDate, endDate).toDays(); i++) {
            LocalDate date = startDate.plusDays(i).toLocalDate();
            if (!result.containsKey(date)) {
                result.put(date, 0L);
            }
        }
        return result;
    }
}
