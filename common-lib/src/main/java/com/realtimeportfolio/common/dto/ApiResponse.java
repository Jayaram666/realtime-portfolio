package com.realtimeportfolio.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;

    private String message;

    private T data;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String resource;

    // Quick helper factory method for successful responses
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data, String resource) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .resource(resource)
                .build();
    }

    // Quick helper factory method for error responses
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String resource,T errorResponse) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data((T) errorResponse)
                .resource(resource)
                .build();
    }
}
