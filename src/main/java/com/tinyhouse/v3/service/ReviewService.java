package com.tinyhouse.v3.service;

import com.tinyhouse.v3.dto.ReviewRequestDto;
import com.tinyhouse.v3.dto.model.House;
import com.tinyhouse.v3.dto.model.Review;
import com.tinyhouse.v3.dto.model.User;
import com.tinyhouse.v3.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final HouseService houseService;

    public ReviewService(ReviewRepository repository, UserService userService, HouseService houseService) {
        this.reviewRepository = repository;
        this.userService = userService;
        this.houseService = houseService;
    }
    public void createReview(ReviewRequestDto dto) {
        User user = userService.getUserById(dto.getUserId());
        House house = houseService.getHouseById(dto.getHouseId());

        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setUser(user);
        review.setHouse(house);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
    }

    public List<Review> getReviewsForHouse(UUID houseId) {
        House house = houseService.getHouseById(houseId);
        return reviewRepository.findByHouse(house);
    }

}
