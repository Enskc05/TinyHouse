package com.tinyhouse.v3.service;

import com.tinyhouse.v3.config.HouseDeletionHandler;
import com.tinyhouse.v3.config.HouseNotFoundException;
import com.tinyhouse.v3.dto.HouseDto;
import com.tinyhouse.v3.dto.HouseListResponse;
import com.tinyhouse.v3.dto.HouseResponseDto;
import com.tinyhouse.v3.model.*;
import com.tinyhouse.v3.repository.HouseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HouseService {
    private final HouseRepository houseRepository;
    private final UserService userService;
    private final HouseDeletionHandler houseDeletionHandler;

    public HouseService(HouseRepository houseRepository,
                        UserService userService,
                        HouseDeletionHandler houseDeletionHandler) {
        this.houseRepository = houseRepository;
        this.userService = userService;
        this.houseDeletionHandler = houseDeletionHandler;
    }

    public HouseResponseDto add(HouseDto request) {
        User owner = userService.findByEmail(request.getOwnerEmail());

        House house = new House(
                UUID.randomUUID(),
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getPricePerNight(),
                request.isActive(),
                request.getAvailableFrom(),
                request.getAvailableTo(),
                LocalDateTime.now(),
                owner,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        List<HouseImage> imageList = request.getImageUrls().stream()
                .filter(this::isValidBase64)
                .map(base64 -> new HouseImage(UUID.randomUUID(), base64, null, house))
                .collect(Collectors.toList());

        house.setImages(imageList);

        House savedHouse = houseRepository.save(house);
        return new HouseResponseDto(savedHouse.getId());
    }

    public void update(UUID houseId, HouseDto dto) {
        House existingHouse = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("House not found"));

        User owner = userService.findByEmail(dto.getOwnerEmail());

        existingHouse.setTitle(dto.getTitle());
        existingHouse.setDescription(dto.getDescription());
        existingHouse.setLocation(dto.getLocation());
        existingHouse.setPricePerNight(dto.getPricePerNight());
        existingHouse.setAvailableFrom(dto.getAvailableFrom());
        existingHouse.setAvailableTo(dto.getAvailableTo());
        existingHouse.setActive(dto.isActive());
        existingHouse.setOwner(owner);

        List<HouseImage> updatedImages = dto.getImageUrls().stream()
                .map(base64 -> new HouseImage(UUID.randomUUID(), base64, null, existingHouse))
                .collect(Collectors.toList());

        existingHouse.getImages().clear(); // Eski görselleri kaldır
        existingHouse.getImages().addAll(updatedImages);

        houseRepository.save(existingHouse);
    }

    @Transactional
    public void delete(UUID houseId) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new HouseNotFoundException(houseId));

        houseDeletionHandler.prepareForDeletion(house);
        houseRepository.delete(house);
    }

    public List<HouseListResponse> getHousesByOwnerEmail(String ownerEmail) {
        User owner = userService.findByEmail(ownerEmail);
        return houseRepository.findByOwner(owner).stream()
                .map(this::convertToHouseListResponse)
                .collect(Collectors.toList());
    }

    private HouseListResponse convertToHouseListResponse(House house) {
        return new HouseListResponse(
                house.getId(),
                house.getTitle(),
                house.getDescription(),
                house.getLocation(),
                house.getPricePerNight(),
                house.isActive(),
                house.getAvailableFrom(),
                house.getAvailableTo(),
                house.getOwner().getEmail()
        );
    }
    private LocalDate calculateNewAvailableFrom(House house) {
        List<Reservation> activeReservations = house.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> r.getEndDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .collect(Collectors.toList());

        if (activeReservations.isEmpty()) {
            return LocalDate.now();
        }

        return activeReservations.get(0).getStartDate();
    }
    private boolean isValidBase64(String value) {
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    @Transactional
    public void updateHouseAvailability(House house) {

        LocalDate newAvailableFrom = calculateNewAvailableFrom(house);

        if (newAvailableFrom != null && !newAvailableFrom.equals(house.getAvailableFrom())) {
            house.setAvailableFrom(newAvailableFrom);
            houseRepository.save(house);
        }
    }
    public House getHouseById(@NotNull UUID houseId) {
        return houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House not found with ID: " + houseId));
    }
    public List<House> getHousesByOwnerId(UUID ownerId) {
        return houseRepository.findByOwnerId(ownerId);
    }
    public List<HouseListResponse> getAllHouses() {
        return houseRepository.findAll().stream()
                .map(this::convertToHouseListResponse)
                .collect(Collectors.toList());
    }
    public void deleteAllByOwner(User owner){
        houseRepository.deleteAllByOwner(owner);
    }
}