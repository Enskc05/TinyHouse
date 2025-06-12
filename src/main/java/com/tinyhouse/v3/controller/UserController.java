package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.UserInfoResponseDto;
import com.tinyhouse.v3.model.User;
import com.tinyhouse.v3.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findByEmail(email);
        user.setStatus(false);
        userService.save(user);

        return ResponseEntity.ok().build();
    }
}
