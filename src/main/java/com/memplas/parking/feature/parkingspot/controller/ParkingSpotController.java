package com.memplas.parking.feature.parkingspot.controller;

import com.memplas.parking.feature.parkingspot.dto.ParkingSpotDto;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.parkingspot.service.ParkingSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking/spots")
@Validated
@Tag(name = "Parking Spots", description = "Parking spot management operations")
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new parking spot")
    public ParkingSpotDto createSpot(@Valid @RequestBody ParkingSpotDto spotDto) {
        return parkingSpotService.createSpot(spotDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parking spot by ID")
    public ParkingSpotDto getSpot(@PathVariable Long id) {
        return parkingSpotService.getSpotById(id);
    }

    @GetMapping
    @Operation(summary = "Get parking spots with filters")
    public Page<ParkingSpotDto> getSpots(
            @Parameter(description = "Facility ID filter") @RequestParam(required = false) Long facilityId,
            @Parameter(description = "Floor ID filter") @RequestParam(required = false) Long floorId,
            @Parameter(description = "Spot type filter") @RequestParam(required = false) SpotType spotType,
            @Parameter(description = "Spot status filter") @RequestParam(required = false) SpotStatus spotStatus,
            @ParameterObject @PageableDefault(sort = "spotNumber") Pageable pageable) {
        return parkingSpotService.findByFilters(facilityId, floorId, spotType, spotStatus, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parking spot")
    public ParkingSpotDto updateSpot(@PathVariable Long id, @Valid @RequestBody ParkingSpotDto spotDto) {
        return parkingSpotService.updateSpot(id, spotDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete parking spot")
    public void deleteSpot(@PathVariable Long id) {
        parkingSpotService.deleteSpot(id);
    }


}