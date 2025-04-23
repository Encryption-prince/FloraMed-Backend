package com.virtual.herbal.garden.Virtual_Herbs_Garden.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
}
