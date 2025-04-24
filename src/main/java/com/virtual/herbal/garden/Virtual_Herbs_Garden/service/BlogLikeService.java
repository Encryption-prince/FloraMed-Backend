package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.BlogLike;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BlogLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogLikeService {

    private final BlogLikeRepository blogLikeRepository;

    public BlogLike addLike(BlogLike like) {
        return blogLikeRepository.save(like);
    }

    public long getLikeCount(Long blogId) {
        return blogLikeRepository.countByBlogId(blogId);
    }

    public boolean hasUserLiked(Long blogId, String userEmail) {
        return blogLikeRepository.existsByBlogIdAndUserEmail(blogId, userEmail);
    }

    public void removeLike(Long blogId, String userEmail) {
        blogLikeRepository.deleteByBlogIdAndUserEmail(blogId, userEmail);
    }
}