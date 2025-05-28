package com.tinyhouse.v3.config;


import java.util.UUID;

public class HouseNotFoundException extends RuntimeException {
    public HouseNotFoundException(UUID houseId) {
        super("House not found with id: " + houseId);
    }
}
