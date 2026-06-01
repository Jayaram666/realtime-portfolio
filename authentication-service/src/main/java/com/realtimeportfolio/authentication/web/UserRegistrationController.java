package com.realtimeportfolio.authentication.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.entity.User;
import com.realtimeportfolio.authentication.repo.UserRepository;
import com.realtimeportfolio.authentication.service.UserRegistrationService;
import com.realtimeportfolio.common.dto.UserDto;
import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import com.realtimeportfolio.common.dto.UserRegistrationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;
    private final UserRepository userRepository;

    public UserRegistrationController(UserRegistrationService userRegistrationService,
                                      UserRepository userRepository) {
        this.userRegistrationService = userRegistrationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse register(@RequestBody UserRegistrationRequest request) {
        log.info("Register API called. email={}", request != null ? request.getEmail() : null);
        UserRegistrationResponse response = userRegistrationService.register(request);
        log.info("Register API completed successfully. userId={}", response.getUserId());
        return response;
    }

    @GetMapping("/by-email")
    public UserDto getUserByEmail(@RequestParam("email") String email) {
        log.info("Get user by email API called. email={}", email);
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Get user by id API called. userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
