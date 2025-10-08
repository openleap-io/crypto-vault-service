package io.openleap.cvs.controller.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(
    name = "ErrorResponse",
    description = "Standard error response format for API errors"
)
public record ErrorResponse(
    @Schema(
        description = "Human-readable error message describing what went wrong",
        example = "Invalid initialization vector"
    )
    String message,
    
    @Schema(
        description = "The fully qualified class name of the exception that occurred",
        example = "io.openleap.cvs.exception.InvalidIvException"
    )
    String exceptionClassName,
    
    @Schema(
        description = "Timestamp when the error occurred",
        example = "2024-01-15T10:30:45.123"
    )
    LocalDateTime exceptionTime
) {}
