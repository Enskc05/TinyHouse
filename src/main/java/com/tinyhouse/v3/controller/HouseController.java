package com.tinyhouse.v3.controller;

import com.tinyhouse.v3.dto.HouseDto;
import com.tinyhouse.v3.dto.HouseListResponse;
import com.tinyhouse.v3.dto.HouseResponseDto;
import com.tinyhouse.v3.service.HouseService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/house")
public class HouseController {
    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping(path = "/add")
    public ResponseEntity<HouseResponseDto> add(@RequestBody HouseDto houseDto){
        return ResponseEntity.ok( houseService.add(houseDto));
    }
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping(path = "/update/{houseId}")
    public ResponseEntity<Void> update(@PathVariable UUID houseId, @RequestBody HouseDto houseDto){
        houseService.update(houseId, houseDto);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping(path = "/delete/{houseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID houseId){
        houseService.delete(houseId);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping(path = "/list")
    public List<HouseListResponse> getCurrentUserHouses(Authentication authentication) {
        return houseService.getHousesByOwnerEmail(authentication.getName());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('RENTER')")
    public List<HouseListResponse> getAllHouses() {
        return houseService.getAllHouses();
    }
}
