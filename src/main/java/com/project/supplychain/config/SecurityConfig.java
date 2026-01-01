package com.project.supplychain.config;

import com.project.supplychain.JWT.JWTConverter;
import com.project.supplychain.security.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTConverter jwtConverter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JWTConverter jwtConverter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtConverter = jwtConverter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                )
                .authorizeHttpRequests(auth -> auth

                        //Inventory
                        .requestMatchers(HttpMethod.GET,"/api/inventories/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/inventories/**").hasRole("WAREHOUSE_MANAGER")

                        //WareHouse
                        .requestMatchers(HttpMethod.GET,"/api/warehouses/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/warehouses/**").hasRole("WAREHOUSE_MANAGER")

                        //Inventory Movements
                        .requestMatchers(HttpMethod.GET,"/api/inventoryMovements/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/inventoryMovements/**").hasRole("WAREHOUSE_MANAGER")

                        //Products
                        .requestMatchers(HttpMethod.GET,"/api/products/**").hasAnyRole("WAREHOUSE_MANAGER","CLIENT", "ADMIN")
                        .requestMatchers("/api/products/**").hasRole("ADMIN")

                        //Sales Order
                        .requestMatchers(HttpMethod.GET,"/api/salesOrders/**").hasAnyRole("WAREHOUSE_MANAGER","ADMIN","CLIENT")
                        .requestMatchers(HttpMethod.POST,"/api/salesOrders/**").hasRole("CLIENT")

                        //Sales Order LINE
                        .requestMatchers(HttpMethod.GET,"/api/salesOrderLines/**").hasAnyRole("WAREHOUSE_MANAGER","ADMIN","CLIENT")
                        .requestMatchers(HttpMethod.POST,"/api/salesOrderLines/**").hasRole("CLIENT")

                        //Supplier (FIXED URLS)
                        .requestMatchers(HttpMethod.GET,"/api/suppliers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/suppliers/**").hasRole("ADMIN")

                        //Purchase Order
                        .requestMatchers(HttpMethod.GET,"/api/purchaseOrders/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/purchaseOrders/**").hasRole("WAREHOUSE_MANAGER")

                        //Purchase Order LINE
                        .requestMatchers(HttpMethod.GET,"/api/purchaseOrderLines/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/purchaseOrderLines/**").hasRole("WAREHOUSE_MANAGER")

                        //Carriers
                        .requestMatchers(HttpMethod.GET,"/api/carriers/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers("/api/carriers/**").hasRole("ADMIN")

                        //Shipment
                        .requestMatchers(HttpMethod.GET,"/api/shipments/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/shipments/**").hasRole("WAREHOUSE_MANAGER")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}