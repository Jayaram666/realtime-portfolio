package com.realtimeportfolio.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSyncRequest {
    private UUID userId;
    private String email;
    private String name;
}