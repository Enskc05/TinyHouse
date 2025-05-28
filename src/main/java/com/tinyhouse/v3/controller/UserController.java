package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.UserInfoResponseDto;
import com.tinyhouse.v3.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/info")
    public ResponseEntity<UserInfoResponseDto> info(){
        return ResponseEntity.ok(userService.getCurrentUserInfo());
    }
}
