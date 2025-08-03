package com.memplas.parking.feature.vehicle.controller;

import com.memplas.parking.feature.vehicle.dto.VehicleDto;
import com.memplas.parking.feature.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@Validated
@Tag(name = "Vehicles", description = "Vehicle management operations")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register vehicle", description = "Registers a new vehicle for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle data"),
            @ApiResponse(responseCode = "409", description = "License plate already exists")
    })
    public VehicleDto createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        return vehicleService.createVehicle(vehicleDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user vehicles", description = "Retrieves all vehicles owned by the current user")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    public List<VehicleDto> getCurrentUserVehicles() {
        return vehicleService.getCurrentUserVehicles();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user vehicles", description = "Retrieves vehicles for specified user. Users can view own vehicles, ADMINs can view any.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<VehicleDto> getUserVehicles(@Parameter(description = "User ID") @PathVariable Long userId) {
        return vehicleService.getUserVehicles(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves vehicle details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleDto getVehicleById(@Parameter(description = "Vehicle ID") @PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle", description = "Updates vehicle details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public VehicleDto updateVehicle(@Parameter(description = "Vehicle ID") @PathVariable Long id, @Valid @RequestBody VehicleDto vehicleDto) {
        return vehicleService.updateVehicle(id, vehicleDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public void deleteVehicle(@Parameter(description = "Vehicle ID") @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}
