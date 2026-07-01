package com.realtimeportfolio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class UserCreatedEvent {

    private UUID userId;
    private String name;
    private String email;
}
