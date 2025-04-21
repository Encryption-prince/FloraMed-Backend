package com.virtual.herbal.garden.Virtual_Herbs_Garden.dto;

import lombok.Data;

@Data
public class FilterBlogRequest {
    private String title;
    private String content;
    private String author;
}
