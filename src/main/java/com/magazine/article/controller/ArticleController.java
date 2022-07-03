package com.magazine.article.controller;

import com.magazine.article.controller.dto.ArticleDto;
import com.magazine.article.controller.mapper.ArticleMapper;
import com.magazine.article.model.Article;
import com.magazine.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @PostMapping
    public ResponseEntity<ArticleDto> saveArticle(@Valid @RequestBody ArticleDto articleDTO) {
        Article article = articleMapper.toArticle(articleDTO);
        Article createdArticle = articleService.createArticle(article);
        return ResponseEntity.ok(articleMapper.toArticleDto(createdArticle));
    }

    @GetMapping
    public ResponseEntity<Page<ArticleDto>> getArticles(@RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<Article> articlesPage = articleService.getArticles(page, size);
        Page<ArticleDto> articlesDtoPage = articlesPage.map(articleMapper::toArticleDto);
        return ResponseEntity.ok(articlesDtoPage);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<LocalDate, Long>> countArticlesPerDateForSevenDays() {
        return ResponseEntity.ok(articleService.countArticlesPerDateForSevenDays());
    }
}
