package com.portfolio.controller;
import com.portfolio.dto.UserRegistrationRequest;
import com.portfolio.dto.UserRegistrationResponse;
import com.portfolio.service.UserRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return userRegistrationService.register(request);
    }
}