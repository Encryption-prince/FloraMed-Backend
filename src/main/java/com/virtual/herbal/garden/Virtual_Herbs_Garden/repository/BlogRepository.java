package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;


import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByTitleContainingIgnoreCase(String title);
    List<Blog> findByContentContainingIgnoreCase(String content);
    List<Blog> findByAuthorContainingIgnoreCase(String author);
    List<Blog> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
}
