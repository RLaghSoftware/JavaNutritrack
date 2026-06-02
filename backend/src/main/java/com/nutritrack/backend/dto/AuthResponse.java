package com.nutritrack.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;
    private long expiresIn;
    private UserResponse user;
}
