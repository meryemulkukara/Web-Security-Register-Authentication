package com.example.basicSecurity.service;

import com.example.basicSecurity.entity.Role;
import com.example.basicSecurity.repository.UserRepository;
import com.example.basicSecurity.request.AuthenticationRequest;
import com.example.basicSecurity.request.RegisterRequest;
import com.example.basicSecurity.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import com.example.basicSecurity.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user= User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        var user= userRepository.findUserByUsername(request.getUsername()).orElseThrow();

        var jtwToken= jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jtwToken)
                .build();
    }
}
