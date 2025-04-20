package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Tag(name = "TEST API")
public class UserController {

    @GetMapping("/profile")
    public String getUserProfile() {
        return "You have successfully accessed a protected endpoint!";
    }
}
