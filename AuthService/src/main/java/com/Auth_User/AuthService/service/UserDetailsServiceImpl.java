package com.Auth_User.AuthService.service;

import com.Auth_User.AuthService.model.User;
import com.Auth_User.AuthService.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserDetails loadUserById(Long id) {
        User u = userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(u.getId()),
                u.getPasswordHash() == null ? "" : u.getPasswordHash(),
                u.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(u.getRole().getName()))
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(username).orElseGet(() -> userRepo.findByPhone(username).orElseThrow(() -> new UsernameNotFoundException("User not found")));
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(u.getId()),
                u.getPasswordHash() == null ? "" : u.getPasswordHash(),
                u.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(u.getRole().getName()))
        );
    }
}

