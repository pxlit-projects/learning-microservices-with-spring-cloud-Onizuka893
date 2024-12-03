package be.pxl.services;


import be.pxl.services.domain.*;
import be.pxl.services.repository.CategoryRepository;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.CategoryService;
import be.pxl.services.services.HeaderValidationService;
import be.pxl.services.services.ProductService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class CategoryIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HeaderValidationService headerValidationService;

    @AfterEach
    public void cleanUpDatabase() {
        categoryRepository.deleteAll();
    }

    private String adminAuthHeader;
    private String userAuthHeader;

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

    @BeforeEach
    void setUp() {
        adminAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("admin:admin".getBytes());
        userAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("user:user".getBytes());
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Arrange - create categories in the database
        CategoryResponse category1 = categoryService.addCategory(new CategoryRequest("Phones"));
        CategoryResponse category2 = categoryService.addCategory(new CategoryRequest("Tablets"));

        // Act & Assert
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Phones"))
                .andExpect(jsonPath("$[1].name").value("Tablets"));
    }

    @Test
    void testCreateCategory_AdminRole_ReturnsCreated() throws Exception {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest("Clothing");
        String categoryRequestJson = objectMapper.writeValueAsString(categoryRequest);

        // Act & Assert
        mockMvc.perform(post("/category")
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Clothing"));
    }

    @Test
    void testCreateCategory_NonAdminRole_ReturnsForbidden() throws Exception {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest("Clothing");
        String categoryRequestJson = objectMapper.writeValueAsString(categoryRequest);

        // Act & Assert
        mockMvc.perform(post("/category")
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCategory_AdminRole_ReturnsOk() throws Exception {
        // Arrange - create category in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category to update")).getId();
        CategoryRequest categoryRequest = new CategoryRequest("Updated Category");
        String categoryRequestJson = objectMapper.writeValueAsString(categoryRequest);

        // Act & Assert
        mockMvc.perform(put("/category/{id}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    void testUpdateCategory_NonAdminRole_ReturnsForbidden() throws Exception {
        // Arrange - create category in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category to update")).getId();
        CategoryRequest categoryRequest = new CategoryRequest("Updated Category");
        String categoryRequestJson = objectMapper.writeValueAsString(categoryRequest);

        // Act & Assert
        mockMvc.perform(put("/category/{id}", categoryId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddProductToCategory_AdminRole_ReturnsOk() throws Exception {
        // Arrange - create category and add product in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category with products")).getId();
        Long productId = productService.addProduct(ProductRequest.builder()
                .name("phone")
                .description("desc")
                .price(100.00)
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .stock(10)
                .build()
        ).getId();

        // Act & Assert
        mockMvc.perform(post("/category/{categoryId}/products/{productId}", categoryId, productId)
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void testAddProductToCategory_NonAdminRole_ReturnsForbidden() throws Exception {
        // Arrange - create category in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category with products")).getId();
        Long productId = 101L;

        // Act & Assert
        mockMvc.perform(post("/category/{categoryId}/products/{productId}", categoryId, productId)
                        .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRemoveProductFromCategory_AdminRole_ReturnsOk() throws Exception {
        // Arrange - create category and add product in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category to remove product from")).getId();
        Long productId = productService.addProduct(ProductRequest.builder()
                .name("phone")
                .description("desc")
                .price(100.00)
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .stock(10)
                .build()
        ).getId();
        categoryService.addProductToCategory(categoryId, productId);

        // Act & Assert
        mockMvc.perform(delete("/category/{categoryId}/products/{productId}", categoryId, productId)
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveProductFromCategory_AdminRole_ReturnsProductNotInCategory() throws Exception {
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category to remove product from")).getId();
        Long productId = productService.addProduct(ProductRequest.builder()
                .name("phone")
                .description("desc")
                .price(100.00)
                .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                .stock(10)
                .build()
        ).getId();

        // Act & Assert
        MvcResult result = mockMvc.perform(delete("/category/{categoryId}/products/{productId}", categoryId, productId)
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "Product does not belong to this category" );
    }

    @Test
    void testRemoveProductFromCategory_AdminRole_ReturnsProductNotFound() throws Exception {
        // Arrange - create category and add product in the database
        Long categoryId = categoryService.addCategory(new CategoryRequest("Category to remove product from")).getId();
        Long productId = 101L;

        // Act & Assert
        mockMvc.perform(delete("/category/{categoryId}/products/{productId}", categoryId, productId)
                        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader))
                .andExpect(status().isNotFound()); // Expect a 404 Not Found response
    }
}
