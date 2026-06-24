package com.realtimeportfolio.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UUID userId;
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String message;

}