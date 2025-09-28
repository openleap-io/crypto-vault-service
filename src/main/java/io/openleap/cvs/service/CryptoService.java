package io.openleap.cvs.service;


import io.openleap.cvs.config.CvsConfig;
import io.openleap.cvs.exception.InvalidIvException;
import io.openleap.cvs.util.AESUtil;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CryptoService {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String OBJECT_ID = "objectId";
    private final SecretKey secretKey;
    private final IvParameterSpec ivParameterSpec;

    public CryptoService(CvsConfig cvsConfig)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException,
            IOException {
        byte[] ivBytes = cvsConfig.getAesInitializationVector().getBytes();

        if (ivBytes.length < 16) {
            throw new InvalidAlgorithmParameterException("Wrong IV length: must be 16 bytes long");
        }

        Path secretKeyPath = Paths.get(cvsConfig.getEncryptionKeyPath());

        secretKey =
                AESUtil.getKeyFromPassword(Files.readString(secretKeyPath, Charset.defaultCharset()));
        ivParameterSpec =
                new IvParameterSpec(Arrays.copyOfRange(ivBytes, ivBytes.length - 16, ivBytes.length));
    }

    public IvParameterSpec generateIvFromSessionUser(String sessionUserId) throws NoSuchAlgorithmException {
        if (sessionUserId == null) {
            return ivParameterSpec;
        }
        // Convert string to bytes
        byte[] inputBytes = sessionUserId.getBytes(StandardCharsets.UTF_8);

        // Hash using SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(inputBytes);

        // Use first 16 bytes for IV (AES block size)
        byte[] iv = new byte[16];
        System.arraycopy(hash, 0, iv, 0, 16);

        return new IvParameterSpec(iv);
    }

    public String encryptWithAesCbc(String clearTextInput, String iv)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        return AESUtil.encrypt(ALGORITHM, clearTextInput, secretKey, generateIvFromSessionUser(iv));
    }

    public String decryptWithAesCbc(String cipherInput, String iv)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                 InvalidKeyException {
        try {
            return AESUtil.decrypt(ALGORITHM, cipherInput, secretKey, generateIvFromSessionUser(iv));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new InvalidIvException(e.getMessage());
        }
    }

    public Map<String, String> decryptWithAesCbc(Map<String, String> cipherInputList, String iv) {
        return cipherInputList.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                v -> v.getKey(),
                                v -> {
                                    try {
                                        return v.getKey() == OBJECT_ID
                                                ? v.getValue()
                                                : decryptWithAesCbc(v.getValue(), iv);
                                    } catch (NoSuchAlgorithmException
                                             | InvalidAlgorithmParameterException
                                             | InvalidKeyException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
    }

    public Map<String, String> encryptWithAesCbc(Map<String, String> cipherInputList, String iv) {
        return cipherInputList.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                v -> v.getKey(),
                                v -> {
                                    try {
                                        return v.getKey() == OBJECT_ID
                                                ? v.getValue()
                                                : encryptWithAesCbc(v.getValue(), iv);
                                    } catch (NoSuchAlgorithmException
                                             | InvalidAlgorithmParameterException
                                             | NoSuchPaddingException
                                             | IllegalBlockSizeException
                                             | BadPaddingException
                                             | InvalidKeyException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
    }
}