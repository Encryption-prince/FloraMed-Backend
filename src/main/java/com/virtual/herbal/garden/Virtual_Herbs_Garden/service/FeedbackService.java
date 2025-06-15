package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Feedback;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}
