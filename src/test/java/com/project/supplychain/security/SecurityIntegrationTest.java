package com.project.supplychain.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supplychain.JWT.JWT;
import com.project.supplychain.enums.Roles;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.AuthRepository;
import com.project.supplychain.repositories.RefreshTokenRepository;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class SecurityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("securitytest")
            .withUsername("test")
            .withPassword("test");

        static {
                postgres.start();
        }

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
                registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWT jwt;

        @Value("${jwt.secret}")
        private String jwtSecret;

    @BeforeEach
    void setup() {
        refreshTokenRepository.deleteAll();
        salesOrderRepository.deleteAll();
        warehouseRepository.deleteAll();
        authRepository.deleteAll();
    }

    @Test
    void loginReturnsTokensForValidCredentials() throws Exception {
        persistClient("client1@test.com", "pwd123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"client1@test.com","password":"pwd123"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void loginFailsWithInvalidPassword() throws Exception {
        persistClient("client2@test.com", "pwd123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"client2@test.com","password":"wrong"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void validTokenAllowsAccess() throws Exception {
        WarehouseManager manager = persistWarehouseManager("manager@test.com", "pwd123");
        String token = jwt.generateToken(manager);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        WarehouseManager manager = persistWarehouseManager("manager2@test.com", "pwd123");

        String expiredToken = Jwts.builder()
                .subject(manager.getEmail())
                .claim("role", manager.getRole().name())
                .issuedAt(java.util.Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .expiration(java.util.Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(jwtSecretBytes()))
                .compact();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void refreshTokenRenewsAccess() throws Exception {
        persistClient("client3@test.com", "pwd123");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"client3@test.com","password":"pwd123"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = loginResult.getResponse().getContentAsString();
        Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {});
        String refreshToken = (String) payload.get("refreshToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void revokedRefreshTokenIsRejected() throws Exception {
        persistClient("client4@test.com", "pwd123");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"client4@test.com","password":"pwd123"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Map<String, Object> payload = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), new TypeReference<>() {}
        );
        String refreshToken = (String) payload.get("refreshToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void roleBasedAccessIsEnforced() throws Exception {
        Client client = persistClient("client5@test.com", "pwd123");
        String token = jwt.generateToken(client);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void clientCannotAccessAnotherClientSalesOrder() throws Exception {
        Client owner = persistClient("owner@test.com", "pwd123");
        Client other = persistClient("other@test.com", "pwd123");

        Warehouse warehouse = new Warehouse();
        warehouse.setCode("W1");
        warehouse.setName("WH-1");
        warehouse.setActive(true);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        SalesOrder order = new SalesOrder();
        order.setClient(owner);
        order.setWarehouse(savedWarehouse);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        String otherToken = jwt.generateToken(other);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/salesOrders/" + savedOrder.getId())
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private Client persistClient(String email, String rawPassword) {
        Client client = Client.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Roles.CLIENT)
                .build();
        return authRepository.save(client);
    }

    private WarehouseManager persistWarehouseManager(String email, String rawPassword) {
        WarehouseManager manager = WarehouseManager.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Roles.WAREHOUSE_MANAGER)
                .build();
        return authRepository.save(manager);
    }

    private byte[] jwtSecretBytes() {
                return jwtSecret.getBytes(StandardCharsets.UTF_8);
    }
}
