package com.portfolio.service;

import com.portfolio.dto.UserRegistrationRequest;
import com.portfolio.dto.UserRegistrationResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserRegistrationService {

    UserRegistrationResponse register(UserRegistrationRequest request);
}
