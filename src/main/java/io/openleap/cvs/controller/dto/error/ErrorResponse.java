package io.openleap.cvs.controller.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message, String exceptionClassName, LocalDateTime exceptionTime) {}
