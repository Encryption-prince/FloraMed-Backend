package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @GetMapping("/profile")
    public String getUserProfile() {
        return "You have successfully accessed a protected endpoint!";
    }
}
