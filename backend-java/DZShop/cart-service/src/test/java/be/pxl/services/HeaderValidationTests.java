package be.pxl.services;

import be.pxl.services.services.HeaderValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderValidationTests {
    private HeaderValidationService headerValidationService;

    @BeforeEach
    void setUp() {
        headerValidationService = new HeaderValidationService();
    }

    @Test
    void testHasAdminRole_WhenAdminCredentials_ReturnsTrue() {
        // Given
        String authorizationHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("admin:adminpass".getBytes());

        // When
        boolean result = headerValidationService.hasAdminRole(authorizationHeader);

        // Then
        assertTrue(result, "The user should have admin role");
    }

    @Test
    void testHasAdminRole_WhenNonAdminCredentials_ReturnsFalse() {
        // Given
        String authorizationHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("user:userpass".getBytes());

        // When
        boolean result = headerValidationService.hasAdminRole(authorizationHeader);

        // Then
        assertFalse(result, "The user should not have admin role");
    }

    @Test
    void testHasAdminRole_WhenNoAuthorizationHeader_ReturnsFalse() {
        // Given
        String authorizationHeader = null;

        // When
        boolean result = headerValidationService.hasAdminRole(authorizationHeader);

        // Then
        assertFalse(result, "If there's no Authorization header, the user shouldn't have admin role");
    }

    @Test
    void testIsLoggedIn_WhenValidAuthorizationHeader_ReturnsUsername() {
        // Given
        String authorizationHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("admin:adminpass".getBytes());

        // When
        String result = headerValidationService.isLoggedIn(authorizationHeader);

        // Then
        assertEquals("admin", result, "The logged-in username should be returned");
    }

    @Test
    void testIsLoggedIn_WhenNoAuthorizationHeader_ReturnsNull() {
        // Given
        String authorizationHeader = null;

        // When
        String result = headerValidationService.isLoggedIn(authorizationHeader);

        // Then
        assertNull(result, "If there's no Authorization header, the result should be null");
    }
}
