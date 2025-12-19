package com.project.supplychain.controller;


import com.project.supplychain.DTOs.RefreshTokenRequest;
import com.project.supplychain.DTOs.usersDTOs.UserLoginDTO;
import com.project.supplychain.DTOs.usersDTOs.UserRegisterDTO;
import com.project.supplychain.JWT.JWT;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.usersMappers.UserMapper;
import com.project.supplychain.models.RefreshToken;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.services.Auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/Auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JWT jwt;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authenticationService;


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


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            HashMap<String , Object> authResponse = authenticationService.refresh(request.refreshToken());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", authResponse.get("newAccessToken"));
            response.put("refreshToken", authResponse.get("newRefreshRawToken"));
            response.put("message", "Token refreshed successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

}
