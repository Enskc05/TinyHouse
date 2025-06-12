package com.tinyhouse.v3.repository;

import com.tinyhouse.v3.model.House;
import com.tinyhouse.v3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {
    List<House> findByOwner(User owner);
    List<House> findByOwnerId(UUID ownerId);
    void deleteAllByOwner(User owner);

}
