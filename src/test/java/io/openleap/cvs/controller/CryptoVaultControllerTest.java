package io.openleap.cvs.controller;

import io.openleap.cvs.controller.dto.DecryptListRequestDto;
import io.openleap.cvs.controller.dto.DecryptRequestDto;
import io.openleap.cvs.controller.dto.EncryptListRequestDto;
import io.openleap.cvs.controller.dto.EncryptRequestDto;
import io.openleap.cvs.exception.InvalidIvException;
import io.openleap.cvs.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CryptoVaultController Unit Tests")
@SuppressWarnings("unchecked")
public class CryptoVaultControllerTest {

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CryptoVaultController cryptoVaultController;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito annotations
    }

    @Test
    @DisplayName("Should successfully encrypt a single value")
    void shouldSuccessfullyEncryptSingleValue() throws Exception {
        // Given
        EncryptRequestDto request = new EncryptRequestDto("Hello, World!", "user123");
        String expectedEncrypted = "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=";

        when(cryptoService.encryptWithAesCbc(anyString(), anyString())).thenReturn(expectedEncrypted);

        // When
        ResponseEntity<String> response = cryptoVaultController.encrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEncrypted, response.getBody());
        verify(cryptoService).encryptWithAesCbc("Hello, World!", "user123");
    }

    @Test
    @DisplayName("Should successfully decrypt a single value")
    void shouldSuccessfullyDecryptSingleValue() throws Exception {
        // Given
        DecryptRequestDto request = new DecryptRequestDto("U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=", "user123");
        String expectedDecrypted = "Hello, World!";

        when(cryptoService.decryptWithAesCbc(anyString(), anyString())).thenReturn(expectedDecrypted);

        // When
        ResponseEntity<String> response = cryptoVaultController.decrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDecrypted, response.getBody());
        verify(cryptoService).decryptWithAesCbc("U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=", "user123");
    }

    @Test
    @DisplayName("Should return bad request when decrypting with invalid IV")
    void shouldReturnBadRequestWhenDecryptingWithInvalidIv() throws Exception {
        // Given
        DecryptRequestDto request = new DecryptRequestDto("U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=", "user123");

        when(cryptoService.decryptWithAesCbc(anyString(), anyString()))
                .thenThrow(new InvalidIvException("Invalid initialization vector"));

        // When
        ResponseEntity<String> response = cryptoVaultController.decrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid initialization vector", response.getBody());
        verify(cryptoService).decryptWithAesCbc("U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=", "user123");
    }

    @Test
    @DisplayName("Should successfully encrypt multiple values")
    void shouldSuccessfullyEncryptMultipleValues() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("field1", "sensitive data 1");
        data.put("objectId", "12345");
        data.put("field2", "sensitive data 2");

        EncryptListRequestDto request = new EncryptListRequestDto(data, "user123");

        Map<String, String> expectedEncrypted = new HashMap<>();
        expectedEncrypted.put("field1", "encrypted_field1");
        expectedEncrypted.put("objectId", "12345");
        expectedEncrypted.put("field2", "encrypted_field2");

        when(cryptoService.encryptWithAesCbc(any(Map.class), anyString())).thenReturn(expectedEncrypted);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.encryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEncrypted, response.getBody());
        verify(cryptoService).encryptWithAesCbc(data, "user123");
    }

    @Test
    @DisplayName("Should successfully decrypt multiple values")
    void shouldSuccessfullyDecryptMultipleValues() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("field1", "encrypted_field1");
        data.put("objectId", "12345");
        data.put("field2", "encrypted_field2");

        DecryptListRequestDto request = new DecryptListRequestDto(data, "user123");

        Map<String, String> expectedDecrypted = new HashMap<>();
        expectedDecrypted.put("field1", "sensitive data 1");
        expectedDecrypted.put("objectId", "12345");
        expectedDecrypted.put("field2", "sensitive data 2");

        when(cryptoService.decryptWithAesCbc(any(Map.class), anyString())).thenReturn(expectedDecrypted);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.decryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDecrypted, response.getBody());
        verify(cryptoService).decryptWithAesCbc(data, "user123");
    }

    @Test
    @DisplayName("Should handle empty map for encryption")
    void shouldHandleEmptyMapForEncryption() throws Exception {
        // Given
        Map<String, String> emptyData = new HashMap<>();
        EncryptListRequestDto request = new EncryptListRequestDto(emptyData, "user123");

        when(cryptoService.encryptWithAesCbc(any(Map.class), anyString())).thenReturn(emptyData);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.encryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(cryptoService).encryptWithAesCbc(emptyData, "user123");
    }

    @Test
    @DisplayName("Should handle empty map for decryption")
    void shouldHandleEmptyMapForDecryption() throws Exception {
        // Given
        Map<String, String> emptyData = new HashMap<>();
        DecryptListRequestDto request = new DecryptListRequestDto(emptyData, "user123");

        when(cryptoService.decryptWithAesCbc(any(Map.class), anyString())).thenReturn(emptyData);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.decryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(cryptoService).decryptWithAesCbc(emptyData, "user123");
    }

    @Test
    @DisplayName("Should handle map with only objectId for encryption")
    void shouldHandleMapWithOnlyObjectIdForEncryption() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("objectId", "12345");

        EncryptListRequestDto request = new EncryptListRequestDto(data, "user123");

        when(cryptoService.encryptWithAesCbc(any(Map.class), anyString())).thenReturn(data);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.encryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(data, response.getBody());
        verify(cryptoService).encryptWithAesCbc(data, "user123");
    }

    @Test
    @DisplayName("Should handle map with only objectId for decryption")
    void shouldHandleMapWithOnlyObjectIdForDecryption() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("objectId", "12345");

        DecryptListRequestDto request = new DecryptListRequestDto(data, "user123");

        when(cryptoService.decryptWithAesCbc(any(Map.class), anyString())).thenReturn(data);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.decryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(data, response.getBody());
        verify(cryptoService).decryptWithAesCbc(data, "user123");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "user123",
        "admin",
        "test-user",
        "user@example.com",
        "user with spaces",
        "user_with_underscores",
        "user-with-dashes",
        "user.with.dots",
        "user123456789",
        "a",
        "",
        "user with special chars !@#$%^&*()"
    })
    @DisplayName("Should handle various session user ID formats for encryption")
    void shouldHandleVariousSessionUserIdFormatsForEncryption(String sessionUserId) throws Exception {
        // Given
        EncryptRequestDto request = new EncryptRequestDto("Test text", sessionUserId);
        String expectedEncrypted = "encrypted_text";

        when(cryptoService.encryptWithAesCbc(anyString(), anyString())).thenReturn(expectedEncrypted);

        // When
        ResponseEntity<String> response = cryptoVaultController.encrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEncrypted, response.getBody());
        verify(cryptoService).encryptWithAesCbc("Test text", sessionUserId);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "user123",
        "admin",
        "test-user",
        "user@example.com",
        "user with spaces",
        "user_with_underscores",
        "user-with-dashes",
        "user.with.dots",
        "user123456789",
        "a",
        "",
        "user with special chars !@#$%^&*()"
    })
    @DisplayName("Should handle various session user ID formats for decryption")
    void shouldHandleVariousSessionUserIdFormatsForDecryption(String sessionUserId) throws Exception {
        // Given
        DecryptRequestDto request = new DecryptRequestDto("encrypted_text", sessionUserId);
        String expectedDecrypted = "Test text";

        when(cryptoService.decryptWithAesCbc(anyString(), anyString())).thenReturn(expectedDecrypted);

        // When
        ResponseEntity<String> response = cryptoVaultController.decrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDecrypted, response.getBody());
        verify(cryptoService).decryptWithAesCbc("encrypted_text", sessionUserId);
    }

    @Test
    @DisplayName("Should propagate encryption exceptions")
    void shouldPropagateEncryptionExceptions() throws Exception {
        // Given
        EncryptRequestDto request = new EncryptRequestDto("Test text", "user123");

        when(cryptoService.encryptWithAesCbc(anyString(), anyString()))
                .thenThrow(new NoSuchAlgorithmException("Algorithm not found"));

        // When & Then
        assertThrows(NoSuchAlgorithmException.class, () -> {
            cryptoVaultController.encrypt(request);
        });
    }

    @Test
    @DisplayName("Should propagate decryption exceptions")
    void shouldPropagateDecryptionExceptions() throws Exception {
        // Given
        DecryptRequestDto request = new DecryptRequestDto("encrypted_text", "user123");

        when(cryptoService.decryptWithAesCbc(anyString(), anyString()))
                .thenThrow(new InvalidKeyException("Invalid key"));

        // When & Then
        assertThrows(InvalidKeyException.class, () -> {
            cryptoVaultController.decrypt(request);
        });
    }

    @Test
    @DisplayName("Should propagate encryption map exceptions")
    void shouldPropagateEncryptionMapExceptions() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("field1", "sensitive data");
        EncryptListRequestDto request = new EncryptListRequestDto(data, "user123");

        when(cryptoService.encryptWithAesCbc(any(Map.class), anyString()))
                .thenThrow(new RuntimeException("Encryption failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            cryptoVaultController.encryptMap(request);
        });
    }

    @Test
    @DisplayName("Should propagate decryption map exceptions")
    void shouldPropagateDecryptionMapExceptions() throws Exception {
        // Given
        Map<String, String> data = new HashMap<>();
        data.put("field1", "encrypted_data");
        DecryptListRequestDto request = new DecryptListRequestDto(data, "user123");

        when(cryptoService.decryptWithAesCbc(any(Map.class), anyString()))
                .thenThrow(new RuntimeException("Decryption failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            cryptoVaultController.decryptMap(request);
        });
    }

    @Test
    @DisplayName("Should handle null request for encryption")
    void shouldHandleNullRequestForEncryption() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            cryptoVaultController.encrypt(null);
        });
    }

    @Test
    @DisplayName("Should handle null request for decryption")
    void shouldHandleNullRequestForDecryption() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            cryptoVaultController.decrypt(null);
        });
    }

    @Test
    @DisplayName("Should handle null request for encryption map")
    void shouldHandleNullRequestForEncryptionMap() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            cryptoVaultController.encryptMap(null);
        });
    }

    @Test
    @DisplayName("Should handle null request for decryption map")
    void shouldHandleNullRequestForDecryptionMap() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            cryptoVaultController.decryptMap(null);
        });
    }

    @Test
    @DisplayName("Should handle large data for encryption")
    void shouldHandleLargeDataForEncryption() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("This is a very long string that contains a lot of text. ");
        }
        String largeText = sb.toString();
        EncryptRequestDto request = new EncryptRequestDto(largeText, "user123");
        String expectedEncrypted = "encrypted_large_text";

        when(cryptoService.encryptWithAesCbc(anyString(), anyString())).thenReturn(expectedEncrypted);

        // When
        ResponseEntity<String> response = cryptoVaultController.encrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEncrypted, response.getBody());
        verify(cryptoService).encryptWithAesCbc(largeText, "user123");
    }

    @Test
    @DisplayName("Should handle large data for decryption")
    void shouldHandleLargeDataForDecryption() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("This is a very long string that contains a lot of text. ");
        }
        String largeText = sb.toString();
        DecryptRequestDto request = new DecryptRequestDto("encrypted_large_text", "user123");

        when(cryptoService.decryptWithAesCbc(anyString(), anyString())).thenReturn(largeText);

        // When
        ResponseEntity<String> response = cryptoVaultController.decrypt(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(largeText, response.getBody());
        verify(cryptoService).decryptWithAesCbc("encrypted_large_text", "user123");
    }

    @Test
    @DisplayName("Should handle large map for encryption")
    void shouldHandleLargeMapForEncryption() throws Exception {
        // Given
        Map<String, String> largeData = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            largeData.put("field" + i, "sensitive data " + i);
        }
        largeData.put("objectId", "12345");

        EncryptListRequestDto request = new EncryptListRequestDto(largeData, "user123");

        when(cryptoService.encryptWithAesCbc(any(Map.class), anyString())).thenReturn(largeData);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.encryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(largeData.size(), response.getBody().size());
        verify(cryptoService).encryptWithAesCbc(largeData, "user123");
    }

    @Test
    @DisplayName("Should handle large map for decryption")
    void shouldHandleLargeMapForDecryption() throws Exception {
        // Given
        Map<String, String> largeData = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            largeData.put("field" + i, "encrypted_data_" + i);
        }
        largeData.put("objectId", "12345");

        DecryptListRequestDto request = new DecryptListRequestDto(largeData, "user123");

        when(cryptoService.decryptWithAesCbc(any(Map.class), anyString())).thenReturn(largeData);

        // When
        ResponseEntity<Map<String, String>> response = cryptoVaultController.decryptMap(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(largeData.size(), response.getBody().size());
        verify(cryptoService).decryptWithAesCbc(largeData, "user123");
    }
}
