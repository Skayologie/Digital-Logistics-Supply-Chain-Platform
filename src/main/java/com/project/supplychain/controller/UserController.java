package com.project.supplychain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/hello")
public class UserController {

    @GetMapping
    public ResponseEntity<?> hello() {
        HashMap<String , Object> response = new HashMap<>();
        response.put("message","Hello World");
        return ResponseEntity.ok(response);
    }
}