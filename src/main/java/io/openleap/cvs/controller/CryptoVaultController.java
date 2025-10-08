package io.openleap.cvs.controller;

import io.openleap.cvs.controller.dto.DecryptListRequestDto;
import io.openleap.cvs.controller.dto.DecryptRequestDto;
import io.openleap.cvs.controller.dto.EncryptListRequestDto;
import io.openleap.cvs.controller.dto.EncryptRequestDto;
import io.openleap.cvs.exception.InvalidIvException;
import io.openleap.cvs.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(
    name = "Crypto Vault Service",
    description = "API for encrypting and decrypting sensitive data using AES-GCM encryption with user-specific initialization vectors"
)
@SecurityRequirement(name = "LocalKeycloakOauth2")
@RestController
@RequestMapping("/api/cvs")
public class CryptoVaultController {
    @Autowired
    private CryptoService cryptoService;

    @Operation(
        summary = "Encrypt a single value",
        description = "Encrypts a single string value using AES-GCM encryption with a user-specific initialization vector. " +
                     "The IV is generated from the provided session user ID using SHA-256 hashing."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Value successfully encrypted",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string", description = "Base64 encoded encrypted value"),
                examples = @ExampleObject(value = "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid input parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - Encryption failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        )
    })
    @PostMapping(value = "/encrypt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> encrypt(
            @Parameter(
                description = "Request containing the value to encrypt and initialization vector",
                required = true,
                schema = @Schema(implementation = EncryptRequestDto.class)
            )
            @RequestBody @Valid EncryptRequestDto encryptRequestDto)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new ResponseEntity<>(
                cryptoService.encryptWithAesCbc(encryptRequestDto.value(), encryptRequestDto.iv()), HttpStatus.OK);
    }

    @Operation(
        summary = "Decrypt a single value",
        description = "Decrypts a single encrypted string value using AES-GCM decryption with a user-specific initialization vector. " +
                     "The IV must match the one used during encryption."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Value successfully decrypted",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string", description = "Decrypted plain text value"),
                examples = @ExampleObject(value = "sensitive data")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid initialization vector or malformed encrypted data",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string"),
                examples = @ExampleObject(value = "Invalid initialization vector")
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - Decryption failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        )
    })
    @PostMapping(value = "/decrypt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> decrypt(
            @Parameter(
                description = "Request containing the encrypted value and initialization vector",
                required = true,
                schema = @Schema(implementation = DecryptRequestDto.class)
            )
            @RequestBody @Valid DecryptRequestDto decryptRequestDto)
            throws InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, InvalidKeyException {
        try {
            var response = cryptoService.decryptWithAesCbc(decryptRequestDto.value(), decryptRequestDto.iv());
            return new ResponseEntity<>(
                    response, HttpStatus.OK);
        } catch (InvalidIvException e) {
            return new ResponseEntity<>("Invalid initialization vector", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
        summary = "Encrypt multiple values",
        description = "Encrypts multiple key-value pairs using AES-GCM encryption. " +
                     "The 'objectId' field is excluded from encryption and returned as-is. " +
                     "All other values are encrypted using the provided initialization vector."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Values successfully encrypted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    description = "Map of encrypted values with the same keys as input",
                    example = "{\"field1\": \"U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=\", \"objectId\": \"12345\", \"field2\": \"U2FsdGVkX1+abc123def456ghi789jkl012mno345pqr678stu901vwx234yz=\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid input parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - Encryption failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        )
    })
    @PostMapping(value = "/encryptList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> encryptMap(
            @Parameter(
                description = "Request containing the map of values to encrypt and initialization vector",
                required = true,
                schema = @Schema(implementation = EncryptListRequestDto.class)
            )
            @RequestBody @Valid EncryptListRequestDto encryptListRequestDto) {
        return new ResponseEntity<>(
                cryptoService.encryptWithAesCbc(encryptListRequestDto.data(), encryptListRequestDto.iv()), HttpStatus.OK);
    }

    @Operation(
        summary = "Decrypt multiple values",
        description = "Decrypts multiple key-value pairs using AES-GCM decryption. " +
                     "The 'objectId' field is excluded from decryption and returned as-is. " +
                     "All other values are decrypted using the provided initialization vector."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Values successfully decrypted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    description = "Map of decrypted values with the same keys as input",
                    example = "{\"field1\": \"sensitive data 1\", \"objectId\": \"12345\", \"field2\": \"sensitive data 2\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid initialization vector or malformed encrypted data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - Decryption failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = io.openleap.cvs.controller.dto.error.ErrorResponse.class)
            )
        )
    })
    @PostMapping(value = "/decryptList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> decryptMap(
            @Parameter(
                description = "Request containing the map of encrypted values and initialization vector",
                required = true,
                schema = @Schema(implementation = DecryptListRequestDto.class)
            )
            @RequestBody @Valid DecryptListRequestDto decryptListRequestDto) {
        return new ResponseEntity<>(
                cryptoService.decryptWithAesCbc(decryptListRequestDto.data(), decryptListRequestDto.iv()), HttpStatus.OK);
    }
}
