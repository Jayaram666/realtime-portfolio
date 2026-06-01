package com.realtimeportfolio.portfolio.client;

import com.realtimeportfolio.common.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authentication-service")
public interface UserClient {

    @GetMapping("/api/v1/users/by-email")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId);
}
