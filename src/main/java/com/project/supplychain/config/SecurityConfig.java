package com.project.supplychain.config;

import com.project.supplychain.JWT.JWTConverter;
import com.project.supplychain.filters.JwtAuthenticationFilter;
import com.project.supplychain.security.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Autowired
    private JWTConverter jwtConverter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails manager = User.withUsername("manager")
                .password(encoder.encode("manager123"))
                .roles("WAREHOUSE_MANAGER")
                .build();

        UserDetails client = User.withUsername("client")
                .password(encoder.encode("client123"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, manager, client);
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
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtConverter)
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        //Auth
                        .requestMatchers("/api/Auth/**").permitAll()

                        //Inventory
                        .requestMatchers(HttpMethod.GET,"/api/inventories/**").hasRole("ADMIN")
                        .requestMatchers("/api/inventories/**").hasRole("WAREHOUSE_MANAGER")

                        //WareHouse
                        .requestMatchers(HttpMethod.GET,"/api/warehouses/**").hasRole("WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/warehouses/**").hasRole("WAREHOUSE_MANAGER")
                        .requestMatchers("/api/warehouses/**").hasRole("ADMIN")

                        //Inventory Movements
                        .requestMatchers(HttpMethod.GET,"/api/inventoryMovements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/inventoryMovements/**").hasRole("WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/inventoryMovements/**").hasRole("WAREHOUSE_MANAGER")

                        //Products
                        .requestMatchers(HttpMethod.GET,"/api/products/**").hasAnyRole("WAREHOUSE_MANAGER","CLIENT")
                        .requestMatchers("/api/products/**").hasRole("ADMIN")

                        //Sales Order
                        .requestMatchers(HttpMethod.GET,"/api/salesOrders/**").hasAnyRole("WAREHOUSE_MANAGER","ADMIN","CLIENT")
                        .requestMatchers(HttpMethod.POST,"/api/salesOrders/**").hasRole("CLIENT")

                        //Sales Order LINE
                        .requestMatchers(HttpMethod.GET,"/api/salesOrderLines/**").hasAnyRole("WAREHOUSE_MANAGER","ADMIN","CLIENT")
                        .requestMatchers(HttpMethod.POST,"/api/salesOrderLines/**").hasRole("CLIENT")

                        //Supplier
                        .requestMatchers("/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/salesOrderLines/**").hasAnyRole("WAREHOUSE_MANAGER")

                        //Purchase Order
                        .requestMatchers(HttpMethod.GET,"/api/purchaseOrders/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/purchaseOrders/**").hasRole("WAREHOUSE_MANAGER")

                        //Purchase Order LINE
                        .requestMatchers(HttpMethod.GET,"/api/purchaseOrderLines/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/purchaseOrderLines/**").hasRole("WAREHOUSE_MANAGER")

                        //Carriers
                        .requestMatchers("/api/purchaseOrderLines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/purchaseOrderLines/**").hasRole("WAREHOUSE_MANAGER")

                        //Shipment
                        .requestMatchers(HttpMethod.GET,"/api/shipments/**").hasAnyRole("ADMIN","WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/shipments/**").hasRole("WAREHOUSE_MANAGER")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

