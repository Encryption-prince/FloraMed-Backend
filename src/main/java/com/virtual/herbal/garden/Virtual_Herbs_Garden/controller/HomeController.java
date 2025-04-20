package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<?> greetmssg(){
        return new ResponseEntity<>("Hellow World", HttpStatus.OK);
    }
}
