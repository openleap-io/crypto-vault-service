package io.openleap.cvs.controller.dto.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Set;

@RestControllerAdvice
public class ControlServerExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(NoSuchAlgorithmException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
    return new ResponseEntity<>(createErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidAlgorithmParameterException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchAlgorithmException(
      InvalidAlgorithmParameterException ex) {
    return new ResponseEntity<>(createErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidKeyException.class)
  public ResponseEntity<ErrorResponse> handleInvalidKeyException(InvalidKeyException ex) {
    return new ResponseEntity<>(createErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex) {
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    String errorMessage = "";

    if (!violations.isEmpty()) {
      StringBuilder builder = new StringBuilder();
      violations.forEach(violation -> builder.append(" " + violation.getMessage()));
      errorMessage = builder.toString();
    } else {
      errorMessage = "ConstraintViolationException occured.";
    }

    return new ResponseEntity<>(createErrorResponse(ex, errorMessage), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    return new ResponseEntity<>(
        createErrorResponse(ex, "An error occurred: " + ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ErrorResponse createErrorResponse(Exception ex) {
    return new ErrorResponse(ex.getMessage(), ex.getClass().getName(), LocalDateTime.now());
  }

  private ErrorResponse createErrorResponse(Exception ex, String message) {
    return new ErrorResponse(message, ex.getClass().getName(), LocalDateTime.now());
  }
}
