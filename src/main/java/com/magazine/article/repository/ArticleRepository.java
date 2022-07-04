package com.magazine.article.repository;

import com.magazine.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByPublicationDateBetween(OffsetDateTime minPublishingDate, OffsetDateTime maxPublishingDate);
}
