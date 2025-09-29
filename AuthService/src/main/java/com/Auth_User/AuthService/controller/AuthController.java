package com.Auth_User.AuthService.controller;




import com.Auth_User.AuthService.dto.*;
import com.Auth_User.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Validated @RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req, @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        AuthResponse res = authService.login(req, deviceId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest, @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        AuthResponse res = authService.refresh(refreshRequest.getRefreshToken(), deviceId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.ok(Map.of("status","LOGGED_OUT"));
    }
}
