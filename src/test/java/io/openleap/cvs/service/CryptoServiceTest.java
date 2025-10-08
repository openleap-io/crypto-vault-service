package io.openleap.cvs.service;

import io.openleap.cvs.config.CvsConfig;
import io.openleap.cvs.exception.InvalidIvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CryptoService Unit Tests")
public class CryptoServiceTest {

    @TempDir
    Path tempDir;

    private CvsConfig cvsConfig;
    private CryptoService cryptoService;
    private Path secretKeyFile;

    @BeforeEach
    void setUp() throws Exception {
        // Create a temporary secret key file
        secretKeyFile = tempDir.resolve("secret.key");
        String secretKey = "ThisIsATestSecretKeyForAESEncryption123456789012345678901234567890";
        Files.writeString(secretKeyFile, secretKey);

        // Create test configuration
        cvsConfig = new CvsConfig();
        cvsConfig.setEncryptionKeyPath(secretKeyFile.toString());
        cvsConfig.setAesInitializationVector("ThisIsATestInitializationVector123456789012345678901234567890");

        // Initialize the service
        cryptoService = new CryptoService(cvsConfig);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt a single string")
    void shouldEncryptAndDecryptSingleString() throws Exception {
        // Given
        String originalText = "Hello, World!";
        String sessionUserId = "user123";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt empty string")
    void shouldEncryptAndDecryptEmptyString() throws Exception {
        // Given
        String originalText = "";
        String sessionUserId = "user123";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt string with special characters")
    void shouldEncryptAndDecryptStringWithSpecialCharacters() throws Exception {
        // Given
        String originalText = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String sessionUserId = "user123";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt unicode string")
    void shouldEncryptAndDecryptUnicodeString() throws Exception {
        // Given
        String originalText = "Unicode: 你好世界 🌍 émojis 🚀";
        String sessionUserId = "user123";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt long string")
    void shouldEncryptAndDecryptLongString() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("This is a very long string that contains a lot of text. ");
        }
        String originalText = sb.toString();
        String sessionUserId = "user123";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should generate consistent IV for same session user")
    void shouldGenerateConsistentIvForSameSessionUser() throws Exception {
        // Given
        String sessionUserId = "user123";

        // When
        GCMParameterSpec iv1 = cryptoService.generateIvFromSessionUser(sessionUserId);
        GCMParameterSpec iv2 = cryptoService.generateIvFromSessionUser(sessionUserId);

        // Then
        assertNotNull(iv1);
        assertNotNull(iv2);
        assertArrayEquals(iv1.getIV(), iv2.getIV());
    }

    @Test
    @DisplayName("Should generate different IVs for different session users")
    void shouldGenerateDifferentIvsForDifferentSessionUsers() throws Exception {
        // Given
        String sessionUserId1 = "user123";
        String sessionUserId2 = "user456";

        // When
        GCMParameterSpec iv1 = cryptoService.generateIvFromSessionUser(sessionUserId1);
        GCMParameterSpec iv2 = cryptoService.generateIvFromSessionUser(sessionUserId2);

        // Then
        assertNotNull(iv1);
        assertNotNull(iv2);
        assertNotEquals(iv1.getIV(), iv2.getIV());
    }

    @Test
    @DisplayName("Should return default IV when session user is null")
    void shouldReturnDefaultIvWhenSessionUserIsNull() throws Exception {
        // When
        GCMParameterSpec iv = cryptoService.generateIvFromSessionUser(null);

        // Then
        assertNotNull(iv);
        // Should return the default IV from configuration
        assertEquals(128, iv.getTLen());
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt map of strings")
    void shouldEncryptAndDecryptMapOfStrings() throws Exception {
        // Given
        Map<String, String> originalData = new HashMap<>();
        originalData.put("field1", "sensitive data 1");
        originalData.put("objectId", "12345");
        originalData.put("field2", "sensitive data 2");
        originalData.put("field3", "sensitive data 3");
        String sessionUserId = "user123";

        // When
        Map<String, String> encrypted = cryptoService.encryptWithAesCbc(originalData, sessionUserId);
        Map<String, String> decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertEquals(originalData.size(), encrypted.size());
        assertEquals(originalData.size(), decrypted.size());

        // Verify objectId is not encrypted
        assertEquals("12345", encrypted.get("objectId"));
        assertEquals("12345", decrypted.get("objectId"));

        // Verify other fields are encrypted
        assertNotEquals("sensitive data 1", encrypted.get("field1"));
        assertNotEquals("sensitive data 2", encrypted.get("field2"));
        assertNotEquals("sensitive data 3", encrypted.get("field3"));

        // Verify decryption works correctly
        assertEquals("sensitive data 1", decrypted.get("field1"));
        assertEquals("sensitive data 2", decrypted.get("field2"));
        assertEquals("sensitive data 3", decrypted.get("field3"));
    }

    @Test
    @DisplayName("Should handle empty map")
    void shouldHandleEmptyMap() {
        // Given
        Map<String, String> originalData = new HashMap<>();
        String sessionUserId = "user123";

        // When
        Map<String, String> encrypted = cryptoService.encryptWithAesCbc(originalData, sessionUserId);
        Map<String, String> decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertTrue(encrypted.isEmpty());
        assertTrue(decrypted.isEmpty());
    }

    @Test
    @DisplayName("Should handle map with only objectId")
    void shouldHandleMapWithOnlyObjectId() {
        // Given
        Map<String, String> originalData = new HashMap<>();
        originalData.put("objectId", "12345");
        String sessionUserId = "user123";

        // When
        Map<String, String> encrypted = cryptoService.encryptWithAesCbc(originalData, sessionUserId);
        Map<String, String> decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertEquals(1, encrypted.size());
        assertEquals(1, decrypted.size());
        assertEquals("12345", encrypted.get("objectId"));
        assertEquals("12345", decrypted.get("objectId"));
    }

    @Test
    @DisplayName("Should handle map with multiple objectId fields")
    void shouldHandleMapWithMultipleObjectIdFields() {
        // Given
        Map<String, String> originalData = new HashMap<>();
        originalData.put("objectId", "12345");
        originalData.put("anotherObjectId", "67890");
        originalData.put("field1", "sensitive data");
        String sessionUserId = "user123";

        // When
        Map<String, String> encrypted = cryptoService.encryptWithAesCbc(originalData, sessionUserId);
        Map<String, String> decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotNull(decrypted);
        assertEquals(3, encrypted.size());
        assertEquals(3, decrypted.size());

        // Only "objectId" should be excluded from encryption
        assertEquals("12345", encrypted.get("objectId"));
        assertEquals("12345", decrypted.get("objectId"));

        // "anotherObjectId" should be encrypted
        assertNotEquals("67890", encrypted.get("anotherObjectId"));
        assertEquals("67890", decrypted.get("anotherObjectId"));

        // "field1" should be encrypted
        assertNotEquals("sensitive data", encrypted.get("field1"));
        assertEquals("sensitive data", decrypted.get("field1"));
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
        "a", // single character
        "", // empty string
        "user with special chars !@#$%^&*()"
    })
    @DisplayName("Should handle various session user ID formats")
    void shouldHandleVariousSessionUserIdFormats(String sessionUserId) throws Exception {
        // Given
        String originalText = "Test text";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should throw InvalidIvException when decrypting with wrong IV")
    void shouldThrowInvalidIvExceptionWhenDecryptingWithWrongIv() throws Exception {
        // Given
        String originalText = "Test text";
        String sessionUserId1 = "user123";
        String sessionUserId2 = "user456";

        // When
        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId1);

        // Then
        assertThrows(InvalidIvException.class, () -> {
            cryptoService.decryptWithAesCbc(encrypted, sessionUserId2);
        });
    }

    @Test
    @DisplayName("Should throw InvalidIvException when decrypting corrupted data")
    void shouldThrowInvalidIvExceptionWhenDecryptingCorruptedData() {
        // Given
        String corruptedData = "ThisIsNotValidEncryptedData";
        String sessionUserId = "user123";

        // When & Then
        assertThrows(InvalidIvException.class, () -> {
            cryptoService.decryptWithAesCbc(corruptedData, sessionUserId);
        });
    }

    @Test
    @DisplayName("Should throw InvalidIvException when decrypting empty string")
    void shouldThrowInvalidIvExceptionWhenDecryptingEmptyString() {
        // Given
        String emptyData = "";
        String sessionUserId = "user123";

        // When & Then
        assertThrows(InvalidIvException.class, () -> {
            cryptoService.decryptWithAesCbc(emptyData, sessionUserId);
        });
    }

    @Test
    @DisplayName("Should throw exception when decrypting null string")
    void shouldThrowExceptionWhenDecryptingNullString() {
        // Given
        String sessionUserId = "user123";

        // When & Then
        assertThrows(Exception.class, () -> {
            cryptoService.decryptWithAesCbc((String) null, sessionUserId);
        });
    }

    @Test
    @DisplayName("Should throw exception when encrypting null string")
    void shouldThrowExceptionWhenEncryptingNullString() {
        // Given
        String sessionUserId = "user123";

        // When & Then
        assertThrows(Exception.class, () -> {
            cryptoService.encryptWithAesCbc((String) null, sessionUserId);
        });
    }

    @Test
    @DisplayName("Should handle round-trip encryption with different session users")
    void shouldHandleRoundTripEncryptionWithDifferentSessionUsers() throws Exception {
        // Given
        String originalText = "Test text";
        String sessionUserId1 = "user123";
        String sessionUserId2 = "user456";

        // When
        String encrypted1 = cryptoService.encryptWithAesCbc(originalText, sessionUserId1);
        String encrypted2 = cryptoService.encryptWithAesCbc(originalText, sessionUserId2);
        String decrypted1 = cryptoService.decryptWithAesCbc(encrypted1, sessionUserId1);
        String decrypted2 = cryptoService.decryptWithAesCbc(encrypted2, sessionUserId2);

        // Then
        assertNotEquals(encrypted1, encrypted2); // Different session users should produce different encrypted values
        assertEquals(originalText, decrypted1);
        assertEquals(originalText, decrypted2);
    }

    @Test
    @DisplayName("Should handle concurrent encryption and decryption")
    void shouldHandleConcurrentEncryptionAndDecryption() throws Exception {
        // Given
        String originalText = "Test text";
        String sessionUserId = "user123";
        int numberOfThreads = 10;
        int numberOfOperations = 100;

        // When
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < numberOfOperations; j++) {
                        String encrypted = cryptoService.encryptWithAesCbc(originalText, sessionUserId);
                        String decrypted = cryptoService.decryptWithAesCbc(encrypted, sessionUserId);
                        assertEquals(originalText, decrypted);
                    }
                } catch (Exception e) {
                    fail("Concurrent operation failed: " + e.getMessage());
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then - if we reach here, all operations succeeded
        assertTrue(true);
    }

    @Test
    @DisplayName("Should throw exception when IV length is less than 32 bytes")
    void shouldThrowExceptionWhenIvLengthIsLessThan32Bytes() throws Exception {
        // Given
        CvsConfig invalidConfig = new CvsConfig();
        invalidConfig.setEncryptionKeyPath(secretKeyFile.toString());
        invalidConfig.setAesInitializationVector("ShortIV"); // Less than 32 bytes

        // When & Then
        assertThrows(InvalidAlgorithmParameterException.class, () -> {
            new CryptoService(invalidConfig);
        });
    }

    @Test
    @DisplayName("Should throw exception when secret key file does not exist")
    void shouldThrowExceptionWhenSecretKeyFileDoesNotExist() {
        // Given
        CvsConfig invalidConfig = new CvsConfig();
        invalidConfig.setEncryptionKeyPath("/nonexistent/path/secret.key");
        invalidConfig.setAesInitializationVector("ThisIsATestInitializationVector123456789012345678901234567890");

        // When & Then
        assertThrows(IOException.class, () -> {
            new CryptoService(invalidConfig);
        });
    }

    @Test
    @DisplayName("Should handle map with null values")
    void shouldHandleMapWithNullValues() {
        // Given
        Map<String, String> originalData = new HashMap<>();
        originalData.put("field1", "sensitive data");
        originalData.put("field2", null);
        originalData.put("objectId", "12345");
        String sessionUserId = "user123";

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            cryptoService.encryptWithAesCbc(originalData, sessionUserId);
        });
    }
}
