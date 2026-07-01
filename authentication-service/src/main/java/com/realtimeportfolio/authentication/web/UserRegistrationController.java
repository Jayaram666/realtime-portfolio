package com.realtimeportfolio.authentication.web;

import com.realtimeportfolio.authentication.dto.OAuthSyncRequest;
import com.realtimeportfolio.authentication.dto.UserSyncResponse;
import com.realtimeportfolio.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.service.UserRegistrationService;
import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import com.realtimeportfolio.common.dto.UserRegistrationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserRegistrationResponse> register(@RequestBody UserRegistrationRequest request) {
        log.info("Register API called. email={}", request != null ? request.getEmail() : null);
        UserRegistrationResponse response = userRegistrationService.register(request);
        log.info("Register API completed successfully. userId={}", response.getUserId());
        return ApiResponse.<UserRegistrationResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("User registered successfully").build();
    }
    @PostMapping("/oauth-sync")
    public ResponseEntity<UserSyncResponse> syncOAuthUser(@RequestBody OAuthSyncRequest request) {
        UserSyncResponse response = userRegistrationService.processOAuthUser(request);
        return ResponseEntity.ok(response);
    }
}
