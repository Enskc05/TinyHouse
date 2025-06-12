package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.ReviewRequestDto;
import com.tinyhouse.v3.model.Review;
import com.tinyhouse.v3.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @PreAuthorize("hasRole('RENTER')")
    @PostMapping("/create")
    public ResponseEntity<Void> createReview(@RequestBody ReviewRequestDto dto) {
        reviewService.createReview(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/house/{houseId}")
    public ResponseEntity<List<Review>> getReviewsForHouse(@PathVariable UUID houseId) {
        return ResponseEntity.ok(reviewService.getReviewsForHouse(houseId));
    }

}
