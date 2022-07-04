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
    public Map<LocalDate, Long> countArticlesPerDate(LocalDateTime startDate, LocalDateTime endDate) {
        OffsetDateTime startOffsetDate = OffsetDateTime.of(startDate, ZoneOffset.UTC).with(LocalTime.MIN);
        OffsetDateTime endOffsetDate = OffsetDateTime.of(endDate, ZoneOffset.UTC).with(LocalTime.MAX);
        List<Article> articles = articleRepository.findByPublicationDateBetween(startOffsetDate, endOffsetDate);
        Map<LocalDate, Long> articlesNumberPerDate = articles.stream()
                                                             .collect(Collectors.groupingBy(article -> article.getPublicationDate().toLocalDate(),
                                                                      Collectors.counting()));
        return fillDatesWithNoArticles(articlesNumberPerDate, startOffsetDate, endOffsetDate);
    }

    private Map<LocalDate, Long> fillDatesWithNoArticles(Map<LocalDate, Long> articlesNumberPerDate,
                                                         OffsetDateTime startDate, OffsetDateTime endDate) {
        Map<LocalDate, Long> result = new HashMap<>(articlesNumberPerDate);
        for (int i = 0; i <= Duration.between(startDate, endDate).toDays(); i++) {
            LocalDate date = startDate.plusDays(i).toLocalDate();
            if (!result.containsKey(date)) {
                result.put(date, 0L);
            }
        }
        return result;
    }
}
