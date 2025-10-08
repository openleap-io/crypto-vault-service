package io.openleap.cvs.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CvsConfig testCvsConfig() throws Exception {
        CvsConfig config = new CvsConfig();
        
        // Set test initialization vector (32 bytes)
        config.setAesInitializationVector("ThisIsATestInitializationVector123456789012345678901234567890");
        
        // Create a temporary secret key file for testing
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path secretKeyFile = tempDir.resolve("test-secret.key");
        
        // Write test secret key to file
        String testSecretKey = "ThisIsATestSecretKeyForAESEncryption123456789012345678901234567890";
        Files.writeString(secretKeyFile, testSecretKey);
        
        config.setEncryptionKeyPath(secretKeyFile.toString());
        
        return config;
    }
}
