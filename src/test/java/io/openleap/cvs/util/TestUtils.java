package io.openleap.cvs.util;

import io.openleap.cvs.config.CvsConfig;
import io.openleap.cvs.service.CryptoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility class for test setup and common test operations
 */
public class TestUtils {

    private static final String TEST_SECRET_KEY = "ThisIsATestSecretKeyForAESEncryption123456789012345678901234567890";
    private static final String TEST_IV = "ThisIsATestInitializationVector123456789012345678901234567890";

    /**
     * Creates a test CvsConfig with temporary files
     *
     * @return CvsConfig configured for testing
     * @throws IOException if file operations fail
     */
    public static CvsConfig createTestCvsConfig() throws IOException {
        CvsConfig config = new CvsConfig();
        
        // Set test initialization vector (32 bytes)
        config.setAesInitializationVector(TEST_IV);
        
        // Create a temporary secret key file for testing
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path secretKeyFile = tempDir.resolve("test-secret-" + System.currentTimeMillis() + ".key");
        
        // Write test secret key to file
        Files.writeString(secretKeyFile, TEST_SECRET_KEY);
        
        config.setEncryptionKeyPath(secretKeyFile.toString());
        
        return config;
    }

    /**
     * Creates a test CvsConfig with a specific secret key file
     *
     * @param secretKeyFile Path to the secret key file
     * @return CvsConfig configured for testing
     */
    public static CvsConfig createTestCvsConfig(Path secretKeyFile) {
        CvsConfig config = new CvsConfig();
        config.setAesInitializationVector(TEST_IV);
        config.setEncryptionKeyPath(secretKeyFile.toString());
        return config;
    }

    /**
     * Creates a CryptoService instance for testing
     *
     * @return CryptoService configured for testing
     * @throws Exception if service creation fails
     */
    public static CryptoService createTestCryptoService() throws Exception {
        CvsConfig config = createTestCvsConfig();
        return new CryptoService(config);
    }

    /**
     * Creates a CryptoService instance for testing with a specific config
     *
     * @param config CvsConfig to use
     * @return CryptoService configured for testing
     * @throws Exception if service creation fails
     */
    public static CryptoService createTestCryptoService(CvsConfig config) throws Exception {
        return new CryptoService(config);
    }

    /**
     * Generates a test secret key file
     *
     * @param tempDir Temporary directory to create the file in
     * @return Path to the created secret key file
     * @throws IOException if file creation fails
     */
    public static Path createTestSecretKeyFile(Path tempDir) throws IOException {
        Path secretKeyFile = tempDir.resolve("test-secret.key");
        Files.writeString(secretKeyFile, TEST_SECRET_KEY);
        return secretKeyFile;
    }

    /**
     * Generates a test secret key file with custom content
     *
     * @param tempDir Temporary directory to create the file in
     * @param secretKey Content of the secret key file
     * @return Path to the created secret key file
     * @throws IOException if file creation fails
     */
    public static Path createTestSecretKeyFile(Path tempDir, String secretKey) throws IOException {
        Path secretKeyFile = tempDir.resolve("test-secret-" + System.currentTimeMillis() + ".key");
        Files.writeString(secretKeyFile, secretKey);
        return secretKeyFile;
    }

    /**
     * Generates test data for encryption/decryption testing
     *
     * @return Map containing test data
     */
    public static java.util.Map<String, String> createTestData() {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        testData.put("field1", "sensitive data 1");
        testData.put("field2", "sensitive data 2");
        testData.put("field3", "sensitive data 3");
        testData.put("objectId", "12345");
        testData.put("specialChars", "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?`~");
        testData.put("unicode", "Unicode: 你好世界 🌍 émojis 🚀");
        return testData;
    }

    /**
     * Generates test data with only objectId
     *
     * @return Map containing only objectId
     */
    public static java.util.Map<String, String> createTestDataWithOnlyObjectId() {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        testData.put("objectId", "12345");
        return testData;
    }

    /**
     * Generates test data with empty values
     *
     * @return Map containing test data with empty values
     */
    public static java.util.Map<String, String> createTestDataWithEmptyValues() {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        testData.put("field1", "");
        testData.put("field2", "   "); // whitespace
        testData.put("objectId", "12345");
        return testData;
    }

    /**
     * Generates large test data for performance testing
     *
     * @param size Number of fields to generate
     * @return Map containing large test data
     */
    public static java.util.Map<String, String> createLargeTestData(int size) {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        for (int i = 0; i < size; i++) {
            testData.put("field" + i, "sensitive data " + i);
        }
        testData.put("objectId", "12345");
        return testData;
    }

    /**
     * Generates a long test string
     *
     * @param length Approximate length of the string
     * @return Long test string
     */
    public static String createLongTestString(int length) {
        StringBuilder sb = new StringBuilder();
        String baseText = "This is a test string that will be repeated to create a long string. ";
        int repetitions = length / baseText.length() + 1;
        for (int i = 0; i < repetitions; i++) {
            sb.append(baseText);
        }
        return sb.toString().substring(0, Math.min(length, sb.length()));
    }

    /**
     * Generates test session user IDs
     *
     * @return Array of test session user IDs
     */
    public static String[] getTestSessionUserIds() {
        return new String[]{
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
        };
    }

    /**
     * Generates test strings with various patterns
     *
     * @return Array of test strings
     */
    public static String[] getTestStrings() {
        return new String[]{
            "Simple text",
            "Text with numbers 123456",
            "Text with symbols !@#$%^&*()",
            "Text with spaces and tabs\t",
            "Text with newlines\nand carriage returns\r",
            "Text with quotes \"double\" and 'single'",
            "Text with backslashes \\ and forward slashes /",
            "Text with equals signs = and plus signs +",
            "Text with percent signs % and ampersands &",
            "Unicode: 你好世界 🌍 émojis 🚀",
            "", // empty string
            "   " // whitespace only
        };
    }

    /**
     * Cleans up test files
     *
     * @param files Files to delete
     */
    public static void cleanupTestFiles(Path... files) {
        for (Path file : files) {
            try {
                if (Files.exists(file)) {
                    Files.delete(file);
                }
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    /**
     * Waits for a specified amount of time
     *
     * @param milliseconds Time to wait in milliseconds
     */
    public static void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
