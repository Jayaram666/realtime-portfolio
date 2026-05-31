package com.portfolio.service;

import com.portfolio.dto.LoginRequest;
import com.portfolio.dto.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

public interface LoginService {
    @Transactional(readOnly = true)
    LoginResponse login(LoginRequest request);
}
