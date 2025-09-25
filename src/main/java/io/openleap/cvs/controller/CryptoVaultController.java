package io.openleap.cvs.controller;

import io.openleap.cvs.controller.dto.DecryptRequestDto;
import io.openleap.cvs.controller.dto.EncryptListRequestDto;
import io.openleap.cvs.controller.dto.EncryptRequestDto;
import io.openleap.cvs.controller.dto.DecryptListRequestDto;
import io.openleap.cvs.service.CryptoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@SecurityRequirement(name = "LocalKeycloakOauth2")
@RestController
@RequestMapping("/api/cvs")
public class CryptoVaultController {
    @Autowired
    private CryptoService cryptoService;

    @PostMapping(value = "/encrypt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> encrypt(@RequestBody @Valid EncryptRequestDto encryptRequestDto)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new ResponseEntity<>(
                cryptoService.encryptWithAesCbc(encryptRequestDto.value(), encryptRequestDto.iv()), HttpStatus.OK);
    }

    @PostMapping(value = "decrypt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> decrypt(@RequestBody @Valid DecryptRequestDto decryptRequestDto)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new ResponseEntity<>(
                cryptoService.decryptWithAesCbc(decryptRequestDto.value(), decryptRequestDto.iv()), HttpStatus.OK);
    }

    @PostMapping(value = "/encryptList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> encryptMap(
            @RequestBody @Valid EncryptListRequestDto encryptListRequestDto) {
        return new ResponseEntity<>(
                cryptoService.encryptWithAesCbc(encryptListRequestDto.data(), encryptListRequestDto.iv()), HttpStatus.OK);
    }

    @PostMapping(value = "/decryptList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> encryptMap(
            @RequestBody @Valid DecryptListRequestDto decryptListRequestDto) {
        return new ResponseEntity<>(
                cryptoService.decryptWithAesCbc(decryptListRequestDto.data(), decryptListRequestDto.iv()), HttpStatus.OK);
    }
}
