package com.realtimeportfolio.authentication.web;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.service.LoginService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("Login API called. email={}", request != null ? request.getEmail() : null);
        LoginResponse response = loginService.login(request);
        log.info("Login API completed successfully. userId={}", response.getUserId());
        return response;
    }
}