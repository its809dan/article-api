package com.magazine.article.controller.mapper;

import com.magazine.article.controller.dto.ArticleDto;
import com.magazine.article.model.Article;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleDto toArticleDto(Article article);

    Article toArticle(ArticleDto articleDTO);
}
