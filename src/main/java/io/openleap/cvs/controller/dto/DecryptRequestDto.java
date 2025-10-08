package io.openleap.cvs.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "DecryptRequest",
    description = "Request payload for decrypting a single encrypted value"
)
public record DecryptRequestDto(
    @Schema(
        description = "The base64 encoded encrypted value to be decrypted",
        example = "U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=",
        required = true
    )
    String value,
    
    @Schema(
        description = "Session user ID used to generate the initialization vector (IV) for decryption. " +
                     "Must match the IV used during encryption.",
        example = "user123",
        required = true
    )
    String iv
) {
}
