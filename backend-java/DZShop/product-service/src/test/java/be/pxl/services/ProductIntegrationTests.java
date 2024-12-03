package be.pxl.services;

import be.pxl.services.domain.EnergyRating;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductRequest;
import be.pxl.services.domain.ProductResponse;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.HeaderValidationService;
import be.pxl.services.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductIntegrationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @MockBean
    private HeaderValidationService headerValidationService;

    @AfterEach
    public void cleanUpDatabase() {
        productRepository.deleteAll();
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

   public String getAdminCredentials() {
       String username = "admin";
       String password = "admin";
       String credentials = username + ":" + password;
       String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
       return "Basic " + encodedCredentials;
   }

    public String getUserCredentials() {
        String username = "user";
        String password = "user";
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    @Test
    @Transactional
    public void testCreateProduct() throws Exception {
        // Arrange: Prepare the product request
        ProductRequest productRequest = ProductRequest.builder()
                .name("Iphone 16")
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .price(1282.99)
                .stock(10)
                .description("Apple iphone 16")
                .build();

        String productRequestAsJson = objectMapper.writeValueAsString(productRequest);

        // Mock header validation to simulate an admin role
        when(headerValidationService.hasAdminRole(getAdminCredentials())).thenReturn(true);

        // Act: Perform the HTTP request to create a product
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .header(HttpHeaders.AUTHORIZATION, getAdminCredentials()) // Correct header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestAsJson))
                .andExpect(status().isCreated())  // Assert the HTTP status is Created
                .andReturn();

        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testCreateProductForbidden() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Iphone 16")
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .price(1282.99)
                .stock(10)
                .description("Apple iphone 16")
                .build();

        String productRequestAsJson = objectMapper.writeValueAsString(productRequest);
        when(headerValidationService.hasAdminRole(getUserCredentials())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .header(HttpHeaders.AUTHORIZATION, getUserCredentials())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestAsJson))
                .andExpect(status().isForbidden());

        assertEquals(0, productRepository.findAll().size());
    }


    @Test
    @Transactional
    public void testAddProductToCart() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Iphone 16")
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .price(1282.99)
                .stock(10)
                .description("Apple iphone 16")
                .build();
        ProductResponse response = productService.addProduct(productRequest);
        Long productId = response.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/{id}/add", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Check if the product is added to the cart (or perform relevant assertions)
        // For example, you can check if the cart contains the product now, or the product status is updated.
        Product product = productRepository.findById(productId).get();
        assertEquals(9, product.getStock());
    }

    @Test
    @Transactional
    public void testRemoveProductFromCart() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Iphone 16")
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .price(1282.99)
                .stock(10)
                .description("Apple iphone 16")
                .build();
        ProductResponse response = productService.addProduct(productRequest);
        Long productId = response.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/{id}/remove", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Product product = productRepository.findById(productId).get();
        assertEquals(11, product.getStock());
    }

    @Test
    @Transactional
    public void testRemoveProductFromCartNotFoundException() throws Exception {
        Long productId = 99L;

        // Perform the HTTP request and expect a 404 Not Found status (assuming the product doesn't exist)
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}/remove", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect a 404 Not Found response

        // Optionally verify that the product still exists in the database (if expected)
        Optional<Product> productOpt = productRepository.findById(productId);
        assertTrue(productOpt.isEmpty()); // Check that the product with this ID does not exist
    }

    @Test
    @Transactional
    public void testAddProductToCartNotFoundException() throws Exception {
        Long productId = 99L;

        // Perform the HTTP request and expect a 404 Not Found status (assuming the product doesn't exist)
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}/add", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect a 404 Not Found response

        // Optionally verify that the product still exists in the database (if expected)
        Optional<Product> productOpt = productRepository.findById(productId);
        assertTrue(productOpt.isEmpty()); // Check that the product with this ID does not exist
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
