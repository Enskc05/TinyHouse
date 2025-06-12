package com.tinyhouse.v3.controller;


import com.tinyhouse.v3.dto.AuthResponseDto;
import com.tinyhouse.v3.dto.LoginRequestDto;
import com.tinyhouse.v3.dto.RegisterRequestDto;
import com.tinyhouse.v3.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<AuthResponseDto> signin(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }


}
