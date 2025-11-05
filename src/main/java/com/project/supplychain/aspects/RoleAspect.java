package com.project.supplychain.aspects;

import com.project.supplychain.JWT.JWT;
import com.project.supplychain.annotations.RoleRequired;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Aspect
@Component
public class RoleAspect {

    @Autowired
    private JWT jwtService;

    @Around("@annotation(roleRequired)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RoleRequired roleRequired) throws Throwable {
        HashMap<String , Object> respo = new HashMap<>();
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        String userRole = jwtService.extractRole(token);

        List<String> allowedRoles = Arrays.asList(roleRequired.value());

        if (allowedRoles.stream().noneMatch(r -> r.equalsIgnoreCase(userRole))) {
            respo.put("message","Access denied: requires role " + allowedRoles);
            respo.put("code", HttpStatus.FORBIDDEN.value());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(respo);
        }

        return joinPoint.proceed();
    }
}
