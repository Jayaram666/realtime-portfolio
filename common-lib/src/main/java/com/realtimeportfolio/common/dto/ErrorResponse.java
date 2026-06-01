package com.realtimeportfolio.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> validationErrors;

    public ErrorResponse() {
    }

    public ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<String> validationErrors
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public ErrorResponse(int value, String validationFailed, List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = value;
        this.error = validationFailed;
        this.validationErrors = errors;
        this.message = "Validation failed for one or more fields";
        this.path = null;
    }

    public <T> ErrorResponse(int value, String name, String userAlreadyExists, List<T> emailAlreadyExistsInDatabase) {

        this.timestamp = LocalDateTime.now();
        this.status = value;
        this.error = name;
        this.message = userAlreadyExists;
        this.validationErrors = new ArrayList<>();
        for (T error : emailAlreadyExistsInDatabase) {
            this.validationErrors.add(error.toString());
        }
        this.path = null;

    }

    /**
     * Used for normal business errors.
     *
     * Example:
     * Stock not found
     * Duplicate stock
     * Invalid ticker symbol
     */
    public static ErrorResponse of(
            int status,
            String error,
            String message,
            String path
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                null
        );
    }

    /**
     * Used for validation errors from @Valid.
     *
     * Example:
     * companyName: Company name is required
     * tickerSymbol: Ticker symbol is required
     */
    public static ErrorResponse validation(
            int status,
            String error,
            String message,
            String path,
            List<String> validationErrors
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                validationErrors
        );
    }

    /**
     * Builder-style method if you want to create response step by step.
     */
    public static ErrorResponse builder() {
        return new ErrorResponse();
    }

    public ErrorResponse timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorResponse status(int status) {
        this.status = status;
        return this;
    }

    public ErrorResponse error(String error) {
        this.error = error;
        return this;
    }

    public ErrorResponse message(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponse path(String path) {
        this.path = path;
        return this;
    }

    public ErrorResponse validationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }

    public ErrorResponse addValidationError(String validationError) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(validationError);
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}