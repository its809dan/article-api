package com.magazine.article.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleCreationDto {

    @Schema(description = "Article title")
    @NotBlank
    @Size(min = 1, max = 100)
    String title;

    @Schema(description = "Article author's name")
    @NotBlank
    String author;

    @Schema(description = "Content of the article")
    @NotBlank
    String content;

    @Schema(description = "Publishing date of the article in the ISO 8601 format", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @NotNull
    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime publicationDate;
}
