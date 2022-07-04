package com.magazine.article.service;

import com.magazine.article.model.Article;
import com.magazine.article.repository.ArticleRepository;
import com.magazine.article.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTests {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository;

    private Article article;
    private List<Article> articles;
    private Page<Article> articlesPage;

    @BeforeEach
    void init() {
        LocalDateTime articlePublicationDate = LocalDateTime.of(2022, Month.JULY, 2, 14, 10, 0);
        article = Article.builder()
                .id(1000L)
                .title("Software Development")
                .author("Allan Pearson")
                .content("This article describes the software development process")
                .publicationDate(OffsetDateTime.of(articlePublicationDate, ZoneOffset.UTC))
                .build();

        LocalDateTime article1PublicationDate = LocalDateTime.of(2022, Month.JUNE, 28, 12, 45, 10);
        Article article1 = Article.builder()
                .id(1001L)
                .title("Spring Framework")
                .author("Bradley Cain")
                .content("This article gives a basic introduction to the Spring Framework")
                .publicationDate(OffsetDateTime.of(article1PublicationDate, ZoneOffset.UTC))
                .build();

        articles = List.of(article, article1);

        articlesPage = new PageImpl<>(articles, PageRequest.of(0, 20), articles.size());
    }

    @Test
    void createArticle_PassedArticle_SavesArticleAndReturnsIt() {
        when(articleRepository.save(article)).thenReturn(article);

        Article createdArticle = articleService.createArticle(article);

        verify(articleRepository, times(1)).save(article);
        assertEquals(1000L, createdArticle.getId());
        assertEquals("Software Development", createdArticle.getTitle());
        assertEquals("Allan Pearson", createdArticle.getAuthor());
        assertEquals("This article describes the software development process", createdArticle.getContent());
        assertEquals(LocalDateTime.of(2022, Month.JULY, 2, 14, 10, 0), createdArticle.getPublicationDate().toLocalDateTime());
        assertEquals(ZoneOffset.UTC, createdArticle.getPublicationDate().getOffset());
    }

    @Test
    void getArticles_SpecifiedPageable_ReturnsPageOfArticles() {
        Pageable page = PageRequest.of(0, 20);

        when(articleRepository.findAll(page)).thenReturn(articlesPage);

        Page<Article> foundArticles = articleService.getArticles(0, 20);

        verify(articleRepository, times(1)).findAll(page);
        assertEquals(2, foundArticles.getTotalElements());
        assertEquals(0, foundArticles.getPageable().getPageNumber());
        assertEquals(20, foundArticles.getPageable().getPageSize());
        assertEquals(articles, foundArticles.getContent());
    }

    @Test
    void countArticlesPerDate_SpecifiedPeriodOfTime_ReturnsMapOfArticlesNumberPerEachDate() {
        LocalDateTime startDate = LocalDateTime.of(2022, Month.JUNE, 27, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, Month.JULY, 3, 14, 10, 0);

        when(articleRepository.findByPublicationDateBetween(any(OffsetDateTime.class), any(OffsetDateTime.class))).thenReturn(articlesPage.getContent());

        Map<LocalDate, Long> articlesCountPerDate = articleService.countArticlesPerDate(startDate, endDate);

        verify(articleRepository, times(1)).findByPublicationDateBetween(any(OffsetDateTime.class), any(OffsetDateTime.class));
        assertEquals(7, articlesCountPerDate.size());
        assertEquals(LocalDate.of(2022, Month.JULY, 3), Collections.max(articlesCountPerDate.keySet()));
        assertEquals(LocalDate.of(2022, Month.JUNE, 27), Collections.min(articlesCountPerDate.keySet()));
        assertEquals(0, articlesCountPerDate.get(LocalDate.of(2022, Month.JULY, 3)));
        assertEquals(1, articlesCountPerDate.get(LocalDate.of(2022, Month.JULY, 2)));
        assertEquals(1, articlesCountPerDate.get(LocalDate.of(2022, Month.JUNE, 28)));
        assertEquals(0, articlesCountPerDate.get(LocalDate.of(2022, Month.JUNE, 27)));
    }
}
