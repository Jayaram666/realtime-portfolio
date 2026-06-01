package com.realtimeportfolio.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {

    private Long userId;
    private String name;
    private String email;
    private String message;
}