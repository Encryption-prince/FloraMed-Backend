package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {
    long countByBlogId(Long blogId);

    boolean existsByBlogIdAndUserEmail(Long blogId, String userEmail);

    void deleteByBlogIdAndUserEmail(Long blogId, String userEmail);
}
