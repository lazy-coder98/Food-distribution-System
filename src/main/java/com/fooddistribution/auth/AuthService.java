package com.fooddistribution.auth;

import com.fooddistribution.auth.dto.AuthResponse;
import com.fooddistribution.auth.dto.LoginRequest;
import com.fooddistribution.auth.dto.RegisterRequest;
import com.fooddistribution.exception.BadRequestException;
import com.fooddistribution.exception.ConflictException;
import com.fooddistribution.security.JwtService;
import com.fooddistribution.security.SecurityUserDetails;
import com.fooddistribution.user.Role;
import com.fooddistribution.user.User;
import com.fooddistribution.user.UserMapper;
import com.fooddistribution.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("Admin users must be created directly by an existing administrator");
        }
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ConflictException("Email is already registered");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(request.role())
                .build();
        User saved = userRepository.save(user);
        SecurityUserDetails userDetails = new SecurityUserDetails(saved);
        return new AuthResponse(jwtService.generateToken(userDetails), "Bearer", UserMapper.toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email().toLowerCase(),
                request.password()
        ));
        User user = userRepository.findByEmail(request.email().toLowerCase()).orElseThrow();
        SecurityUserDetails userDetails = new SecurityUserDetails(user);
        return new AuthResponse(jwtService.generateToken(userDetails), "Bearer", UserMapper.toResponse(user));
    }
}
