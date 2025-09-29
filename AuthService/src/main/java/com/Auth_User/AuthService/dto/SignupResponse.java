package com.Auth_User.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupResponse {
    private Long userId;
    private String status;

    public static SignupResponse of(Long id, String status){
        return new SignupResponse(id, status);
    }
}
