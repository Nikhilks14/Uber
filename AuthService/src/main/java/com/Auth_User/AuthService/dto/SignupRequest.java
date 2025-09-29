package com.Auth_User.AuthService.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String phone;
    private String password;
    private String fullName;
    private String role = "ROLE_USER";

}
