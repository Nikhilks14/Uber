package com.Auth_User.AuthService.controller;

import com.Auth_User.AuthService.model.User;
import com.Auth_User.AuthService.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepo;

    // public UserController(UserRepository userRepo) {
    //        this.userRepo = userRepo;
    //    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication auth) {
        Long id = Long.parseLong(auth.getName());
        return ResponseEntity.of(userRepo.findById(id));
    }
}
