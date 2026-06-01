package com.realtimeportfolio.authentication.service;


import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);
}
