package io.openleap.cvs.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AESUtil Unit Tests")
public class AESUtilTest {

    private SecretKey secretKey;
    private GCMParameterSpec ivParameterSpec;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String TEST_PASSWORD = "ThisIsATestPasswordForAESKeyGeneration123456789012345678901234567890";
    private static final String TEST_IV = "12345678901234567890123456789012"; // 32 bytes

    @BeforeEach
    void setUp() {
        secretKey = AESUtil.getKeyFromPassword(TEST_PASSWORD);
        ivParameterSpec = new GCMParameterSpec(128, TEST_IV.getBytes());
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt a simple string")
    void shouldEncryptAndDecryptSimpleString() throws Exception {
        // Given
        String originalText = "Hello, World!";

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

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

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

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

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

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

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

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

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt string with forward slashes")
    void shouldEncryptAndDecryptStringWithForwardSlashes() throws Exception {
        // Given
        String originalText = "Path/to/file/with/slashes";

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
        // Verify that the encrypted string is URL-safe (no forward slashes)
        assertFalse(encrypted.contains("/"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Simple text",
        "Text with numbers 123456",
        "Text with symbols !@#$%^&*()",
        "Text with spaces and tabs\t",
        "Text with newlines\nand carriage returns\r",
        "Text with quotes \"double\" and 'single'",
        "Text with backslashes \\ and forward slashes /",
        "Text with equals signs = and plus signs +",
        "Text with percent signs % and ampersands &"
    })
    @DisplayName("Should encrypt and decrypt various text patterns")
    void shouldEncryptAndDecryptVariousTextPatterns(String originalText) throws Exception {
        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);
        String decrypted = AESUtil.decrypt(ALGORITHM, encrypted, secretKey, ivParameterSpec);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    @DisplayName("Should generate consistent secret key from password")
    void shouldGenerateConsistentSecretKeyFromPassword() {
        // Given
        String password = "TestPassword123456789012345678901234567890"; // 32+ bytes

        // When
        SecretKey key1 = AESUtil.getKeyFromPassword(password);
        SecretKey key2 = AESUtil.getKeyFromPassword(password);

        // Then
        assertNotNull(key1);
        assertNotNull(key2);
        assertEquals(key1.getAlgorithm(), key2.getAlgorithm());
        assertArrayEquals(key1.getEncoded(), key2.getEncoded());
    }

    @Test
    @DisplayName("Should generate different secret keys for different passwords")
    void shouldGenerateDifferentSecretKeysForDifferentPasswords() {
        // Given
        String password1 = "Password123456789012345678901234567890"; // 32+ bytes
        String password2 = "Password223456789012345678901234567890"; // 32+ bytes

        // When
        SecretKey key1 = AESUtil.getKeyFromPassword(password1);
        SecretKey key2 = AESUtil.getKeyFromPassword(password2);

        // Then
        assertNotNull(key1);
        assertNotNull(key2);
        assertNotEquals(key1.getEncoded(), key2.getEncoded());
    }

    @Test
    @DisplayName("Should handle null password gracefully")
    void shouldHandleNullPasswordGracefully() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            AESUtil.getKeyFromPassword(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when using wrong algorithm")
    void shouldThrowExceptionWhenUsingWrongAlgorithm() {
        // Given
        String wrongAlgorithm = "AES/CBC/PKCS5Padding";
        String originalText = "Test text";

        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.encrypt(wrongAlgorithm, originalText, secretKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should throw exception when using wrong key")
    void shouldThrowExceptionWhenUsingWrongKey() throws Exception {
        // Given
        String originalText = "Test text";
        SecretKey wrongKey = AESUtil.getKeyFromPassword("DifferentPassword123456789012345678901234567890");
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);

        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(ALGORITHM, encrypted, wrongKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should throw exception when using wrong IV")
    void shouldThrowExceptionWhenUsingWrongIV() throws Exception {
        // Given
        String originalText = "Test text";
        GCMParameterSpec wrongIV = new GCMParameterSpec(128, "WrongIV123456789012345678901234".getBytes());
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);

        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(ALGORITHM, encrypted, secretKey, wrongIV);
        });
    }

    @Test
    @DisplayName("Should throw exception when decrypting corrupted data")
    void shouldThrowExceptionWhenDecryptingCorruptedData() {
        // Given
        String corruptedData = "ThisIsNotValidEncryptedData";

        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(ALGORITHM, corruptedData, secretKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should throw exception when decrypting empty string")
    void shouldThrowExceptionWhenDecryptingEmptyString() {
        // Given
        String emptyData = "";

        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(ALGORITHM, emptyData, secretKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should throw exception when decrypting null string")
    void shouldThrowExceptionWhenDecryptingNullString() {
        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(ALGORITHM, null, secretKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should throw exception when encrypting null string")
    void shouldThrowExceptionWhenEncryptingNullString() {
        // When & Then
        assertThrows(Exception.class, () -> {
            AESUtil.encrypt(ALGORITHM, null, secretKey, ivParameterSpec);
        });
    }

    @Test
    @DisplayName("Should produce URL-safe encoded output")
    void shouldProduceUrlSafeEncodedOutput() throws Exception {
        // Given
        String originalText = "Test text with special chars /+=&";

        // When
        String encrypted = AESUtil.encrypt(ALGORITHM, originalText, secretKey, ivParameterSpec);

        // Then
        assertNotNull(encrypted);
        // Should not contain characters that need URL encoding
        assertFalse(encrypted.contains(" "));
        assertFalse(encrypted.contains("/"));
        assertFalse(encrypted.contains("+"));
        assertFalse(encrypted.contains("="));
        assertFalse(encrypted.contains("&"));
    }

    @Test
    @DisplayName("Should handle round-trip encryption with different IVs")
    void shouldHandleRoundTripEncryptionWithDifferentIVs() throws Exception {
        // Given
        String originalText = "Test text";
        GCMParameterSpec iv1 = new GCMParameterSpec(128, "IV123456789012345678901234567890".getBytes());
        GCMParameterSpec iv2 = new GCMParameterSpec(128, "IV987654321098765432109876543210".getBytes());

        // When
        String encrypted1 = AESUtil.encrypt(ALGORITHM, originalText, secretKey, iv1);
        String encrypted2 = AESUtil.encrypt(ALGORITHM, originalText, secretKey, iv2);
        String decrypted1 = AESUtil.decrypt(ALGORITHM, encrypted1, secretKey, iv1);
        String decrypted2 = AESUtil.decrypt(ALGORITHM, encrypted2, secretKey, iv2);

        // Then
        assertNotEquals(encrypted1, encrypted2); // Different IVs should produce different encrypted values
        assertEquals(originalText, decrypted1);
        assertEquals(originalText, decrypted2);
    }
}
