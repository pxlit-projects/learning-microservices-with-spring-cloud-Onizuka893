package be.pxl.services;

import be.pxl.services.domain.Entry;
import be.pxl.services.domain.EntryResponse;
import be.pxl.services.domain.LogbookEntryRequest;
import be.pxl.services.repository.EntryRepository;
import be.pxl.services.services.EntryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class LogbookIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private EntryService entryService;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    @Transactional
    public void testCreateEntry() throws Exception {
        LogbookEntryRequest request = LogbookEntryRequest.builder()
                .message("test")
                .producer("test")
                .build();

        String requestAsJson = objectMapper.writeValueAsString(request);

        // Act: Perform the HTTP request to create a product
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsJson))
                .andExpect(status().isCreated());  // Assert the HTTP status is Created
    }

    @Test
    @Transactional
    public void testGetEntries() throws Exception {
        LogbookEntryRequest request1 = LogbookEntryRequest.builder()
                .message("entry1")
                .producer("desc1")
                .build();

        LogbookEntryRequest request2 = LogbookEntryRequest.builder()
                .message("entry2")
                .producer("desc2")
                .build();
        entryService.addEntry(request1);
        entryService.addEntry(request2);
        // Act: Perform the HTTP request to create a product
       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();// Assert the HTTP status is Created

        List<EntryResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<EntryResponse>>() {});
        assertEquals(2, responses.size());
        assertEquals("entry1", responses.get(0).getMessage());
        assertEquals("entry2", responses.get(1).getMessage());
    }

    @Test
    @Transactional
    void testSaveAndRetrieveEntry() {
        // Arrange
        Entry entry = Entry.builder()
                .message("Test Message")
                .producer("Test Producer")
                .created(new Date())
                .build();

        // Act
        Entry savedEntry = entryRepository.save(entry);
        Entry retrievedEntry = entryRepository.findById(savedEntry.getId()).orElse(null);

        // Assert
        assertNotNull(retrievedEntry);
        assertEquals("Test Message", retrievedEntry.getMessage());
        assertEquals("Test Producer", retrievedEntry.getProducer());
    }
}
