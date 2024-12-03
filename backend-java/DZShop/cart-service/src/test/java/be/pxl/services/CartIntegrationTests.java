package be.pxl.services;

import be.pxl.services.model.EnergyRating;
import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.repository.CartRepository;
import be.pxl.services.services.CartService;
import be.pxl.services.services.HeaderValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class CartIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Container
    private static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:13.2");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Use "create-drop" for testing
    }

    @AfterEach
    public void cleanUpDatabase() {
        cartRepository.deleteAll();
    }

    private String userAuthHeader;

    @BeforeEach
    void setUp() {
        cartService.createCart(1L);
        userAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("user:user".getBytes());
    }

    @Test
    void testGetCart() throws Exception {
        Long cartId = cartService.createCart(10L).getId(); // Ensure that you have a cart with ID 1 in your test data
        mockMvc.perform(get("/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId));
    }

    @Test
    void testGetCart_NotFoundException() throws Exception {
        Long cartId = 101L; // Ensure that you have a cart with ID 1 in your test data
        mockMvc.perform(get("/{id}", cartId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCartByUserId_ValidAuth() throws Exception {
        Long userId = 1L; // Use a valid userId
        mockMvc.perform(get("/user/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testGetCartByUserId_ValidAuth_NotFoundException() throws Exception {
        Long userId = 101L; // Use a valid userId
        mockMvc.perform(get("/user/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCartByUserId_Forbidden() throws Exception {
        Long userId = 1L; // Use a valid userId
        mockMvc.perform(get("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCart_ValidAuth() throws Exception {
        Long userId = 1L; // Use a valid userId
        mockMvc.perform(post("/user/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testCreateCart_Forbidden() throws Exception {
        Long userId = 1L; // Use a valid userId
        mockMvc.perform(post("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFromString_ValidRatings() {
        assertEquals(EnergyRating.A_PLUS_PLUS, EnergyRating.fromString("A++"));
        assertEquals(EnergyRating.A_PLUS, EnergyRating.fromString("A+"));
        assertEquals(EnergyRating.A, EnergyRating.fromString("A"));
        assertEquals(EnergyRating.B, EnergyRating.fromString("B"));
        assertEquals(EnergyRating.C, EnergyRating.fromString("C"));
        assertEquals(EnergyRating.D, EnergyRating.fromString("D"));
        assertEquals(EnergyRating.E, EnergyRating.fromString("E"));
    }

    @Test
    void testFromString_InvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> EnergyRating.fromString("F"));
    }
}
