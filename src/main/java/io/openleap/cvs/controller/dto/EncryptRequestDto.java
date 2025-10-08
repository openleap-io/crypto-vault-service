package io.openleap.cvs.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "EncryptRequest",
    description = "Request payload for encrypting a single value"
)
public record EncryptRequestDto(
    @Schema(
        description = "The plain text value to be encrypted",
        example = "sensitive data",
        required = true
    )
    String value,
    
    @Schema(
        description = "Session user ID used to generate the initialization vector (IV) for encryption. " +
                     "The IV is derived from this value using SHA-256 hashing.",
        example = "user123",
        required = true
    )
    String iv
) {}
