package com.magazine.article.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {

    @Schema(description = "Surrogate identifier of the article")
    Long id;

    @Schema(description = "Article title")
    String title;

    @Schema(description = "Article author's name")
    String author;

    @Schema(description = "Content of the article")
    String content;

    @Schema(description = "Publishing date of the article in the ISO 8601 format", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime publicationDate;
}
