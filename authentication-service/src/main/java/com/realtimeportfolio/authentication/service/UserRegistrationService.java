package com.realtimeportfolio.authentication.service;


import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import com.realtimeportfolio.common.dto.UserRegistrationResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserRegistrationService {

    UserRegistrationResponse register(UserRegistrationRequest request);
}
