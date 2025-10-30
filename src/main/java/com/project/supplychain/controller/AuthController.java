package com.project.supplychain.controller;


import com.project.supplychain.DTOs.usersDTOs.UserLoginDTO;
import com.project.supplychain.DTOs.usersDTOs.UserRegisterDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.usersMappers.UserMapper;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.services.Auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("api/Auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;


    @PostMapping("Register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO dto){
        try{
            HashMap<String , Object> result = authService.register(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
    @PostMapping("Login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto){
        try{
            HashMap<String , Object> result = authService.login(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
    @GetMapping("JWTCheck")
    public ResponseEntity<?> checkJWT(@RequestHeader String token){
        try{
            HashMap<String , Object> result = authService.checkTheUser(token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}
