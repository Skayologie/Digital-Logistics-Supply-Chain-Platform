package com.project.supplychain.services.Auth;

import com.project.supplychain.DTOs.usersDTOs.UserLoginDTO;
import com.project.supplychain.DTOs.usersDTOs.UserRegisterDTO;
import com.project.supplychain.JWT.JWT;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.usersMappers.UserMapper;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWT jwt;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public HashMap<String , Object> register(UserRegisterDTO userDto){
        try{
            Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(userDto);
            if(!violations.isEmpty()){
                String errorMsg = violations.iterator().next().getMessage();
                throw new BadRequestException(errorMsg);
            }
            if(authRepository.existsByEmailIgnoreCase(userDto.getEmail())){
                throw new BadRequestException("Email Already exists , choose another one and try again .");
            }
            if (userDto.getRole() == null) {
                throw new BadRequestException("Role is required");
            }

            HashMap<String , Object> response = new HashMap<>();
            User user = userMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

            if(authRepository.save(user) !=  null){
                response.put("message","Account has been created successfully");
                response.put("status",true);
                return response;
            }else{
                throw new BadRequestException("Failed to create account try again .");
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    public HashMap<String , Object> login(UserLoginDTO userDto){
        try{
            Set<ConstraintViolation<UserLoginDTO>> violations = validator.validate(userDto);
            if(!violations.isEmpty()){
                String errorMsg = violations.iterator().next().getMessage();
                throw new BadRequestException(errorMsg);
            }
            if(!authRepository.existsByEmailIgnoreCase(userDto.getEmail())){
                throw new BadRequestException("The email not exist please try the signup first .");
            }
            User user = authRepository.getByEmail(userDto.getEmail());
            if(!passwordEncoder.matches(userDto.getPassword(),user.getPassword())){
                throw new BadRequestException("The password is incorrect use a valid password for this email .");
            }
            if(user instanceof WarehouseManager){
                HashMap<String , Object> response = new HashMap<>();
                response.put("message","You logged successfully to the account .");
                response.put("status",true);
                response.put("token",jwt.generateToken(user));
                return response;
            } else if (user instanceof Client) {
                HashMap<String , Object> response = new HashMap<>();
                response.put("message","You logged successfully to the account .");
                response.put("status",true);
                response.put("token",jwt.generateToken(user));
                return response;
            } else{
                HashMap<String , Object> response = new HashMap<>();
                response.put("message","You logged successfully to the account .");
                response.put("status",true);
                response.put("token",jwt.generateToken(user));
                return response;
            }



        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public HashMap<String , Object> checkTheUser(String token){
        HashMap<String , Object> response = new HashMap<>();
        Claims claims = jwt.extractAllClaims(token);
        String email = claims.getSubject();
        User user = authRepository.getByEmail(email);

        if(user instanceof WarehouseManager){
            response.put("message","You logged successfully to the account .");
            response.put("status",true);
            response.put("email",email);
            response.put("role",((WarehouseManager) user).getRole());
        } else if (user instanceof Client) {
            response.put("message","You logged successfully to the account .");
            response.put("status",true);
            response.put("email",email);
            response.put("role",((Client) user).getRole());
        } else{
            response.put("message","You logged successfully to the account .");
            response.put("status",true);
            response.put("email",email);
            response.put("role",null);
        }

        return response;
    }
}
