package com.realtimeportfolio.authentication.service;


import com.realtimeportfolio.authentication.dto.TokenRefreshRequest;
import com.realtimeportfolio.authentication.dto.TokenRefreshResponse;
import com.realtimeportfolio.authentication.exception.TokenRefreshException;
import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request) ;
}
