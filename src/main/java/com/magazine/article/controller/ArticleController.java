package com.magazine.article.controller;

import com.magazine.article.controller.dto.ArticleCreationDto;
import com.magazine.article.controller.dto.ArticleDto;
import com.magazine.article.controller.mapper.ArticleMapper;
import com.magazine.article.model.Article;
import com.magazine.article.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/articles")
@Validated
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @Operation(summary = "Create an article with the specified parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The article is successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to access the resource", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden to access the resource", content = @Content)})
    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleCreationDto articleDTO) {
        Article article = articleMapper.toArticle(articleDTO);
        Article createdArticle = articleService.createArticle(article);
        return new ResponseEntity<>(articleMapper.toArticleDto(createdArticle), HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve a list of articles on the certain page with the specified parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied", content = @Content)})
    @GetMapping
    public ResponseEntity<Page<ArticleDto>> getArticles(@Parameter(description = "Page number on which articles are to be retrieved")
                                                        @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
                                                        @Parameter(description = "Number of articles per page")
                                                        @RequestParam(name = "size", defaultValue = "20") @Min(1) int size) {
        Page<Article> articlesPage = articleService.getArticles(page, size);
        Page<ArticleDto> articlesDtoPage = articlesPage.map(articleMapper::toArticleDto);
        return ResponseEntity.ok(articlesDtoPage);
    }

    @Operation(summary = "Count the number of published articles per each date for the last 7 days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/stats")
    public ResponseEntity<Map<LocalDate, Long>> countArticlesPerDateForSevenDays() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(6);
        return ResponseEntity.ok(articleService.countArticlesPerDate(startDate, endDate));
    }
}
