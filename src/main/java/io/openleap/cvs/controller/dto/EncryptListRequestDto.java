package io.openleap.cvs.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(
    name = "EncryptListRequest",
    description = "Request payload for encrypting multiple key-value pairs"
)
public record EncryptListRequestDto(
    @Schema(
        description = "Map of key-value pairs to be encrypted. The 'objectId' field will be excluded from encryption.",
        example = "{\"field1\": \"sensitive data 1\", \"objectId\": \"12345\", \"field2\": \"sensitive data 2\"}",
        required = true
    )
    Map<String, String> data,
    
    @Schema(
        description = "Session user ID used to generate the initialization vector (IV) for encryption. " +
                     "The IV is derived from this value using SHA-256 hashing.",
        example = "user123",
        required = true
    )
    String iv
) {
}
