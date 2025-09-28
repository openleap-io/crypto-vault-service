package io.openleap.cvs.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtil {
    private static final String BACKSLASH_REPLACEMENT = "+_01+";

    private AESUtil() {
    }

    public static String encrypt(String algorithm, String input, SecretKey key, GCMParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());

        var base64 = Base64.getEncoder().encodeToString(cipherText);
        var backslashReplaced = base64.replace("/", BACKSLASH_REPLACEMENT);

        return URLEncoder.encode(backslashReplaced, StandardCharsets.UTF_8);
    }

    public static String decrypt(
            String algorithm, String cipherText, SecretKey key, GCMParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        var urlDecoded = URLDecoder.decode(cipherText, StandardCharsets.UTF_8);
        var backslashRestored = urlDecoded.replace(BACKSLASH_REPLACEMENT, "/");
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(backslashRestored));

        return new String(plainText);
    }

    public static SecretKey getKeyFromPassword(String password) {
        return new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), 0, 32, "AES");
    }
}
