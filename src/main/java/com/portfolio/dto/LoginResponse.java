package com.portfolio.dto;


public class LoginResponse {

    private Long userId;
    private String name;
    private String email;
    private String token;
    private String tokenType;
    private String message;

    public LoginResponse(Long userId,
                         String name,
                         String email,
                         String token,
                         String tokenType,
                         String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.token = token;
        this.tokenType = tokenType;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getMessage() {
        return message;
    }
}