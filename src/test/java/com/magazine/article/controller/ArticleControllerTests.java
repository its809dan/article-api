package com.magazine.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.magazine.article.controller.dto.ArticleCreationDto;
import com.magazine.article.controller.dto.ArticleDto;
import com.magazine.article.controller.mapper.ArticleMapper;
import com.magazine.article.model.Article;
import com.magazine.article.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTests {

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ArticleMapper articleMapper;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ArticleCreationDto articleCreationDto;
    private Article article;
    private ArticleDto articleDto;
    private Map<LocalDate, Long> articlesCountPerDate;

    @BeforeEach
    void initBeforeEach() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime articlePublicationDate = LocalDateTime.of(2022, Month.JULY, 2, 14, 10, 0);
        articleCreationDto = ArticleCreationDto.builder()
                .title("Software Development")
                .author("Allan Pearson")
                .content("This article describes the software development process")
                .publicationDate(OffsetDateTime.of(articlePublicationDate, ZoneOffset.UTC))
                .build();

        article = Article.builder()
                .id(1000L)
                .title("Software Development")
                .author("Allan Pearson")
                .content("This article describes the software development process")
                .publicationDate(OffsetDateTime.of(articlePublicationDate, ZoneOffset.UTC))
                .build();

        articleDto = ArticleDto.builder()
                .id(1000L)
                .title("Software Development")
                .author("Allan Pearson")
                .content("This article describes the software development process")
                .publicationDate(OffsetDateTime.of(articlePublicationDate, ZoneOffset.UTC))
                .build();

        articlesCountPerDate = new HashMap<>();
        articlesCountPerDate.put(LocalDate.of(2022, Month.JULY, 3), 0L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JULY, 2), 3L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JULY, 1), 7L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JUNE, 30), 0L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JUNE, 29), 2L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JUNE, 28), 0L);
        articlesCountPerDate.put(LocalDate.of(2022, Month.JUNE, 27), 9L);
    }

    @Test
    @WithMockUser
    void createArticle_AuthenticatedUserAndValidArticle_CreatesArticleAndReturnsItInResponseWith201StatusCode() throws Exception {
        String json = objectMapper.writeValueAsString(articleCreationDto);

        when(articleMapper.toArticle(articleCreationDto)).thenReturn(article);
        when(articleService.createArticle(article)).thenReturn(article);
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Software Development")))
                .andExpect(jsonPath("$.author", is("Allan Pearson")))
                .andExpect(jsonPath("$.content", is("This article describes the software development process")))
                .andExpect(jsonPath("$.publicationDate", is("2022-07-02T14:10:00Z")));

        verify(articleMapper, times(1)).toArticle(articleCreationDto);
        verify(articleService, times(1)).createArticle(article);
        verify(articleMapper, times(1)).toArticleDto(article);
    }

    @Test
    void createArticle_UnauthenticatedUser_ResponsesWith401StatusCode() throws Exception {
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isUnauthorized());

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    @WithMockUser
    void createArticle_ArticleWithEmptyTitle_RespondsWith400StatusCode(String title) throws Exception {
        articleCreationDto.setTitle(title);
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @Test
    @WithMockUser
    void createArticle_ArticleWithTitleExceeding100SymbolsLength_RespondsWith400StatusCode() throws Exception {
        articleCreationDto.setTitle("A".repeat(101));
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    @WithMockUser
    void createArticle_ArticleWithEmptyAuthor_RespondsWith400StatusCode(String author) throws Exception {
        articleCreationDto.setAuthor(author);
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    @WithMockUser
    void createArticle_ArticleWithEmptyContent_RespondsWith400StatusCode(String content) throws Exception {
        articleCreationDto.setContent(content);
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @Test
    @WithMockUser
    void createArticle_ArticleWithoutPublicationDate_RespondsWith400StatusCode() throws Exception {
        articleCreationDto.setPublicationDate(null);
        String json = objectMapper.writeValueAsString(articleCreationDto);

        mockMvc.perform(post("/articles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleMapper, times(0)).toArticle(articleCreationDto);
        verify(articleService, times(0)).createArticle(article);
        verify(articleMapper, times(0)).toArticleDto(article);
    }

    @Test
    void getArticles_NoPageableParameters_ReturnsPageOfArticlesWithDefaultPageableParametersInResponseWith200StatusCode() throws Exception {
        Page<Article> articlesPage = new PageImpl<>(List.of(article), PageRequest.of(0, 20), 1);

        when(articleService.getArticles(0, 20)).thenReturn(articlesPage);
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Software Development")))
                .andExpect(jsonPath("$.content[0].author", is("Allan Pearson")))
                .andExpect(jsonPath("$.content[0].content", is("This article describes the software development process")))
                .andExpect(jsonPath("$.content[0].publicationDate", is("2022-07-02T14:10:00Z")));

        verify(articleService, times(1)).getArticles(0, 20);
        verify(articleMapper, times(1)).toArticleDto(article);
    }

    @Test
    void getArticles_WithPageableParameters_ReturnsPageOfArticlesWithDefaultPageableParametersInResponseWith200StatusCode() throws Exception {
        Page<Article> articlesPage = new PageImpl<>(List.of(article), PageRequest.of(1, 10), 11);

        when(articleService.getArticles(1, 10)).thenReturn(articlesPage);
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        mockMvc.perform(get("/articles")
                .queryParam("page", "1")
                .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(11)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Software Development")))
                .andExpect(jsonPath("$.content[0].author", is("Allan Pearson")))
                .andExpect(jsonPath("$.content[0].content", is("This article describes the software development process")))
                .andExpect(jsonPath("$.content[0].publicationDate", is("2022-07-02T14:10:00Z")));

        verify(articleService, times(1)).getArticles(1, 10);
        verify(articleMapper, times(1)).toArticleDto(article);
    }

    @Test
    void getArticles_InvalidPageParameter_RespondsWith400StatusCode() throws Exception {
        mockMvc.perform(get("/articles")
                .queryParam("page", "-1")
                .queryParam("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleService, times(0)).getArticles(anyInt(), anyInt());
        verify(articleMapper, times(0)).toArticleDto(any(Article.class));
    }

    @Test
    void getArticles_InvalidSizeParameter_RespondsWith400StatusCode() throws Exception {
        mockMvc.perform(get("/articles")
                .queryParam("page", "0")
                .queryParam("size", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        verify(articleService, times(0)).getArticles(anyInt(), anyInt());
        verify(articleMapper, times(0)).toArticleDto(any(Article.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void countArticlesPerDateForSevenDays_AuthenticatedUserWithAdminRole_ReturnsMapOfArticlesNumberPerEachDateInSevenDaysInResponseWith200StatusCode() throws Exception {
        when(articleService.countArticlesPerDate(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(articlesCountPerDate);

        mockMvc.perform(get("/articles/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", aMapWithSize(7)))
                .andExpect(jsonPath("$.[\"2022-07-03\"]]", is(0)))
                .andExpect(jsonPath("$.[\"2022-07-02\"]]", is(3)))
                .andExpect(jsonPath("$.[\"2022-07-01\"]]", is(7)))
                .andExpect(jsonPath("$.[\"2022-06-30\"]]", is(0)))
                .andExpect(jsonPath("$.[\"2022-06-29\"]]", is(2)))
                .andExpect(jsonPath("$.[\"2022-06-28\"]]", is(0)))
                .andExpect(jsonPath("$.[\"2022-06-27\"]]", is(9)));

        verify(articleService, times(1)).countArticlesPerDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser
    void countArticlesPerDateForSevenDays_AuthenticatedUserWithUserRole_ResponsesWith403StatusCode() throws Exception {
        mockMvc.perform(get("/articles/stats"))
                .andExpect(status().isForbidden());

        verify(articleService, times(0)).countArticlesPerDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void countArticlesPerDateForSevenDays_UnauthenticatedUser_ResponsesWith401StatusCode() throws Exception {
        mockMvc.perform(get("/articles/stats"))
                .andExpect(status().isUnauthorized());

        verify(articleService, times(0)).countArticlesPerDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
