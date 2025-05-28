package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.dto.model.User;
import com.tinyhouse.v3.dto.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(UserRole userRole);
}
