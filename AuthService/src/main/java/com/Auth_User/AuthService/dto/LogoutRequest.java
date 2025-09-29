package com.Auth_User.AuthService.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
