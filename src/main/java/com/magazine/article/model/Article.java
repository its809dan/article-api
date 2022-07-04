package com.magazine.article.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(min = 1, max = 100)
    String title;

    @Column(nullable = false)
    @NotBlank
    String author;

    @Column(nullable = false)
    @NotBlank
    String content;

    @Column(nullable = false)
    @NotNull
    OffsetDateTime publicationDate;
}
