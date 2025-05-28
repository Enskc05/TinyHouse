package com.tinyhouse.v3.service;



import com.tinyhouse.v3.dto.UserInfoResponseDto;
import com.tinyhouse.v3.dto.model.User;
import com.tinyhouse.v3.dto.model.UserRole;
import com.tinyhouse.v3.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserInfoResponseDto getCurrentUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        return new UserInfoResponseDto(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));
    }
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public User getUserById(@NotNull UUID renterId) {
        return userRepository.findById(renterId)
                .filter(user -> user.getRole() == UserRole.RENTER)
                .orElseThrow(() -> new RuntimeException("Renter not found or not authorized"));
    }
    public User getUserByIdOwner(@NotNull UUID renterId) {
        return userRepository.findById(renterId)
                .filter(user -> user.getRole() == UserRole.OWNER)
                .orElseThrow(() -> new RuntimeException("Renter not found or not authorized"));
    }
}
