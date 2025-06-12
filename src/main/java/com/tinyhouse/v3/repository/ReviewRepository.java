package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.model.House;
import com.tinyhouse.v3.model.Review;
import com.tinyhouse.v3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByHouse(House house);
    void deleteAllByUser(User user);
}
