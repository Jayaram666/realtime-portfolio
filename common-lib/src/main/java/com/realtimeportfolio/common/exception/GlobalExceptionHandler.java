package com.realtimeportfolio.common.exception;

import com.realtimeportfolio.common.dto.ApiResponse;
import com.realtimeportfolio.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(ex.getMessage());

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                request.getRequestURI(),
                errorResponse
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Database constraint violation during save or update operation: {}", ex.getMessage());

        ApiResponse<String> apiResponse = ApiResponse.error(
                HttpStatus.CONFLICT,
                "Field value already exists",
                request.getRequestURI(),
                ex.getMessage()
        );

        // FIX: Replaced .ofNullable() with explicit status to guarantee 409 Conflict wire status
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(ex.getMessage());

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                request.getRequestURI(),
                response
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(ex.getMessage());

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.CONFLICT,
                "Duplicate resource",
                request.getRequestURI(),
                response
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage(),
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> err = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Business exception occurred",
                request.getRequestURI(),
                response
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        ErrorResponse response = ErrorResponse.validation(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "Validation failed",
                request.getRequestURI(),
                validationErrors
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                response
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(ex.getMessage());

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Invalid argument",
                request.getRequestURI(),
                response
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessValidationException(
            ValidationException validationException,
            HttpServletRequest httpServletRequest
    ) {
        // FIX: Read structured error messages from the custom ValidationException payload
        List<String> errors = validationException.getValidationMessages();

        ErrorResponse errorResponse = ErrorResponse.validation(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "Validation failed",
                httpServletRequest.getRequestURI(),
                errors
        );

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                httpServletRequest.getRequestURI(),
                errorResponse
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        ApiResponse<String> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI(),
                "Internal server error"+ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
