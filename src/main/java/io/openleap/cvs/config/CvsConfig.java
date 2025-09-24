package io.openleap.cvs.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cvs")
public class CvsConfig {
    @NotBlank
    private String encryptionKeyPath;
    @NotBlank
    private String aesInitializationVector;

    public String getAesInitializationVector() {
        return aesInitializationVector;
    }

    public void setAesInitializationVector(String aesInitializationVector) {
        this.aesInitializationVector = aesInitializationVector;
    }

    public String getEncryptionKeyPath() {
        return encryptionKeyPath;
    }

    public void setEncryptionKeyPath(String encryptionKeyPath) {
        this.encryptionKeyPath = encryptionKeyPath;
    }
}
