package com.realtimeportfolio.authentication.web;


import com.realtimeportfolio.authentication.dto.TokenRefreshRequest;
import com.realtimeportfolio.authentication.dto.TokenRefreshResponse;
import com.realtimeportfolio.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        log.info("Login API called. email={}", request != null ? request.getEmail() : null);
        LoginResponse response = authenticationService.login(request);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.success(HttpStatus.OK, "Login successful", response, httpServletRequest.getRequestURI());
        log.info("Login API completed successfully. userId={}", response.getUserId());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshAccessToken(@RequestBody TokenRefreshRequest request, HttpServletRequest httpServletRequest) {
        log.info("Refresh token API called.");
        TokenRefreshResponse tokenRefreshResponse = authenticationService.refreshAccessToken(request);
        ApiResponse<TokenRefreshResponse> apiResponse = ApiResponse.success(HttpStatus.OK, "Token refreshed successfully", tokenRefreshResponse, httpServletRequest.getRequestURI());
        log.info("Refresh token API completed successfully.");
        return ResponseEntity.ok(apiResponse);
    }
}