package com.memplas.parking.feature.parkingspot.controller;

import com.memplas.parking.feature.parkingspot.dto.ParkingSpotDto;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.parkingspot.service.ParkingSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spots")
@Validated
@Tag(name = "Parking Spots", description = "Parking spot availability operations")
public class ParkingSpotController {
    private final ParkingSpotService spotService;

    public ParkingSpotController(ParkingSpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping("/facility/{facilityId}/available")
    @Operation(summary = "Find available spots", description = "Retrieves available parking spots for a facility, optionally filtered by spot type. Public endpoint.")
    @ApiResponse(responseCode = "200", description = "Available spots retrieved successfully")
    public List<ParkingSpotDto> findAvailableSpots(
            @Parameter(description = "Facility ID") @PathVariable Long facilityId,
            @Parameter(description = "Filter by spot type (optional)") @RequestParam(required = false) SpotType spotType) {
        return spotService.findAvailableSpots(facilityId, spotType);
    }
}
