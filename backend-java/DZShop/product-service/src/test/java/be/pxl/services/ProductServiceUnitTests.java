package be.pxl.services;

import be.pxl.services.domain.EnergyRating;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductServiceUnitTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
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
    public void testGetProducts() throws Exception {
        List<ProductResponse> mockProducts = List.of(
                ProductResponse.builder()
                        .id(1L)
                        .energyRating(EnergyRating.A_PLUS_PLUS.toString())
                        .stock(3)
                        .name("product 1")
                        .description("description 1")
                        .price(199.99)
                        .build(),
                ProductResponse.builder()
                        .id(2L)
                        .energyRating(EnergyRating.A_PLUS.toString())
                        .stock(2)
                        .name("product 2")
                        .description("description 2")
                        .price(299.99)
                        .build()
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("product 2"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testUpdateProductWithAdminRole() throws Exception {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = ProductRequest.builder()
                .name("Updated Product")
                .price(249.99)
                .description("Updated description")
                .stock(5)
                .energyRating(EnergyRating.A_PLUS.toString())
                .build();

        ProductResponse updatedProductResponse = ProductResponse.builder()
                .id(productId)
                .name("Updated Product")
                .price(249.99)
                .description("Updated description")
                .stock(5)
                .energyRating(EnergyRating.A_PLUS.toString())
                .build();

        String productRequestAsJson = new ObjectMapper().writeValueAsString(productRequest);
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("admin:password".getBytes());

        when(headerValidationService.hasAdminRole(authorizationHeader)).thenReturn(true);
        when(productService.updateProduct(productId, productRequest)).thenReturn(updatedProductResponse);

        // Act & Assert
        mockMvc.perform(put("/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(249.99))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.stock").value(5))
                .andExpect(jsonPath("$.energyRating").value(EnergyRating.A_PLUS.toString()));

        verify(headerValidationService, times(1)).hasAdminRole(authorizationHeader);
        verify(productService, times(1)).updateProduct(productId, productRequest);
    }

    @Test
    public void testUpdateProductWithoutAdminRole() throws Exception {
        // Arrange
        Long productId = 1L;
        ProductRequest productRequest = ProductRequest.builder()
                .name("Updated Product")
                .price(249.99)
                .description("Updated description")
                .stock(5)
                .energyRating(EnergyRating.A_PLUS.toString())
                .build();

        String productRequestAsJson = new ObjectMapper().writeValueAsString(productRequest);
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());

        when(headerValidationService.hasAdminRole(authorizationHeader)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestAsJson))
                .andExpect(status().isForbidden());

        verify(headerValidationService, times(1)).hasAdminRole(authorizationHeader);
        verify(productService, times(0)).updateProduct(anyLong(), any());
    }
}
