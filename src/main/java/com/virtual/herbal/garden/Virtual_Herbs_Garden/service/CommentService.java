package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Comment;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByBlogId(Long blogId) {
        return commentRepository.findByBlogId(blogId);
    }

    public void deleteComment(Long commentId, String authorEmail) {
        commentRepository.deleteByIdAndAuthorEmail(commentId, authorEmail);
    }
}
