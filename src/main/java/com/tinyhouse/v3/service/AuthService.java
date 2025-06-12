package com.tinyhouse.v3.service;


import com.tinyhouse.v3.dto.AuthResponseDto;
import com.tinyhouse.v3.dto.LoginRequestDto;
import com.tinyhouse.v3.dto.RegisterRequestDto;
import com.tinyhouse.v3.model.User;
import com.tinyhouse.v3.model.UserRole;
import com.tinyhouse.v3.repository.UserRepository;
import com.tinyhouse.v3.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoderConfig;
    private final JwtService token;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passswordEncoderConfig, JwtService token, UserDetailsService userDetailsService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoderConfig = passswordEncoderConfig;
        this.token = token;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    public Optional<User> getByUserEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User register(RegisterRequestDto request){
        User newUser = new User(
                UUID.randomUUID(),
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                passwordEncoderConfig.encode(request.getPassword()),
                request.getRole() != null ? request.getRole() : UserRole.RENTER,
                true,
                LocalDateTime.now(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        return userRepository.save(newUser);
    }
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getEmail());
        String jwt = token.generateToken(userDetails);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();
        userService.activateUser(user);

        return new AuthResponseDto(jwt, user.getId());
    }

}
