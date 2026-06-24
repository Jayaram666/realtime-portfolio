package com.realtimeportfolio.authentication.web;

import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.service.UserRegistrationService;
import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import com.realtimeportfolio.common.dto.UserRegistrationResponse;
import org.springframework.http.HttpStatus;
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
    public UserRegistrationResponse register(@RequestBody UserRegistrationRequest request) {
        log.info("Register API called. email={}", request != null ? request.getEmail() : null);
        UserRegistrationResponse response = userRegistrationService.register(request);
        log.info("Register API completed successfully. userId={}", response.getUserId());
        return response;
    }
}
