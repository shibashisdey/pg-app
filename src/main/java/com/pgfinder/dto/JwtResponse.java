package com.pgfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String email;
    private String name;
    private String userType;
    private Long userId;
    private boolean isVerified;

    public JwtResponse(String accessToken, String refreshToken, String email, String name, String userType, Long userId, boolean isVerified) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.userId = userId;
        this.isVerified = isVerified;
    }
}
