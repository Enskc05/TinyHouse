package com.tinyhouse.v3.service;



import com.tinyhouse.v3.config.UserNotFoundException;
import com.tinyhouse.v3.dto.UpdateUserDto;
import com.tinyhouse.v3.dto.UserInfoResponseDto;
import com.tinyhouse.v3.model.User;
import com.tinyhouse.v3.model.UserRole;
import com.tinyhouse.v3.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
                user.getId(),
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
    public void save(User user) {
        userRepository.save(user);
    }
    public void delete(User user){
        userRepository.delete(user);
    }
    public void activateUser(User user) {
        user.setStatus(true);
        save(user);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + userId));
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
    public void update(UUID id, UpdateUserDto requestDto){
        User existingUser  = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        existingUser.setName(requestDto.getName());
        existingUser.setSurname(requestDto.getSurname());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setStatus(requestDto.getStatus());
        userRepository.save(existingUser);
    }
    public List<UserInfoResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserInfoResponseDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus(),
                        user.getCreatedAt()
                ))
                .toList();
    }

}
