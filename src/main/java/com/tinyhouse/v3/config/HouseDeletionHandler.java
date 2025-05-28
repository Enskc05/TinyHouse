package com.tinyhouse.v3.config;

import com.tinyhouse.v3.dto.model.House;
import com.tinyhouse.v3.dto.model.User;
import com.tinyhouse.v3.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class HouseDeletionHandler {

    private final UserRepository userRepository;

    public HouseDeletionHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void prepareForDeletion(House house) {
        clearHouseRelations(house);
        removeFromOwner(house);
    }

    private void clearHouseRelations(House house) {
        house.getReservations().clear();
        house.getReviews().clear();
        house.getImages().clear();
    }

    private void removeFromOwner(House house) {
        User owner = house.getOwner();
        owner.getHouses().remove(house);
        userRepository.save(owner);
    }
}
