package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BookmarkedPlantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkedPlantRepository bookmarkRepository;

    @Transactional
    public void removeBookmark(String email, Long plantId) {
        bookmarkRepository.deleteByUserEmailAndPlant_Id(email, plantId);
    }
}

