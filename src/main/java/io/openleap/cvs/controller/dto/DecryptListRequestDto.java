package io.openleap.cvs.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(
    name = "DecryptListRequest",
    description = "Request payload for decrypting multiple key-value pairs"
)
public record DecryptListRequestDto(
    @Schema(
        description = "Map of key-value pairs to be decrypted. The 'objectId' field will be excluded from decryption.",
        example = "{\"field1\": \"U2FsdGVkX1+vupppZksvRf5pq5g5XjFRlipRkwB0K1Y=\", \"objectId\": \"12345\", \"field2\": \"U2FsdGVkX1+abc123def456ghi789jkl012mno345pqr678stu901vwx234yz=\"}",
        required = true
    )
    Map<String, String> data,
    
    @Schema(
        description = "Session user ID used to generate the initialization vector (IV) for decryption. " +
                     "Must match the IV used during encryption.",
        example = "user123",
        required = true
    )
    String iv
) {
}
