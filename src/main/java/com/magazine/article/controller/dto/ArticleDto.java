package com.magazine.article.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
public class ArticleDto {

    Long id;

    @NotBlank
    @Size(max = 100)
    String title;

    @NotBlank
    String author;

    @NotBlank
    String content;

    @NotNull
    @PastOrPresent
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime publishingDate;
}
