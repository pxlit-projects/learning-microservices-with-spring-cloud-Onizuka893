package be.pxl.services;

import be.pxl.services.client.ProductClient;
import be.pxl.services.model.EnergyRating;
import be.pxl.services.model.Product;
import be.pxl.services.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CartServiceUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @MockBean
    private ProductClient productClient;

    private String userAuthHeader;

    @BeforeEach
    void setUp() {
        cartService.createCart(1L);
        userAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("user:user".getBytes());
    }

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

    @Test
    void testAddItemToCart_ValidAuth() throws Exception {
        Long userId = 1L;
        Long productId = 101L; // Use an existing productId
        Product product = Product.builder()
                .id(productId)
                .name("test")
                .description("test")
                .energyRating(EnergyRating.A_PLUS.toString())
                .price(100.00)
                .build();

        when(productClient.addProductToCart(productId)).thenReturn(product);

        mockMvc.perform(post("/user/{userId}/{productId}/add", userId, productId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testAddItemToCart_Forbidden() throws Exception {
        Long userId = 1L;
        Long productId = 101L; // Use an existing productId
        Product product = Product.builder()
                .id(productId)
                .name("test")
                .description("test")
                .energyRating(EnergyRating.A_PLUS.toString())
                .price(100.00)
                .build();

        when(productClient.addProductToCart(productId)).thenReturn(product);

        mockMvc.perform(post("/user/{userId}/{productId}/add", userId, productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRemoveItemFromCart_ValidAuth() throws Exception {
        Long userId = 1L;
        Long productId = 101L; // Use an existing productId
        Product product = Product.builder()
                .id(productId)
                .name("test")
                .description("test")
                .energyRating(EnergyRating.A_PLUS.toString())
                .price(100.00)
                .build();

        when(productClient.addProductToCart(productId)).thenReturn(product);

        mockMvc.perform(post("/user/{userId}/{productId}/remove", userId, productId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testRemoveItemFromCart_Forbidden() throws Exception {
        Long userId = 1L;
        Long productId = 101L; // Use an existing productId
        Product product = Product.builder()
                .id(productId)
                .name("test")
                .description("test")
                .energyRating(EnergyRating.A_PLUS.toString())
                .price(100.00)
                .build();

        when(productClient.addProductToCart(productId)).thenReturn(product);

        mockMvc.perform(post("/user/{userId}/{productId}/remove", userId, productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
