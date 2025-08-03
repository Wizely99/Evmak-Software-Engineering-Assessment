package com.memplas.parking.feature.facility.controller;

import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;
import com.memplas.parking.feature.facility.service.ParkingFacilityService;
import com.memplas.parking.feature.parkingspot.dto.FloorSpotTypeCounts;
import com.memplas.parking.feature.parkingspot.dto.NearbyFacilitiesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/facilities")
@Validated
@Tag(name = "Parking Facilities", description = "Parking facility management and availability")
public class ParkingFacilityController {
    private final ParkingFacilityService facilityService;

    public ParkingFacilityController(ParkingFacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create facility", description = "Creates a new parking facility. Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Facility created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid facility data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or FACILITY_MANAGER role required")
    })
    public ParkingFacilityDto createFacility(@Valid @RequestBody ParkingFacilityDto facilityDto) {
        return facilityService.createFacility(facilityDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get facility by ID", description = "Retrieves facility details by ID. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facility retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    public ParkingFacilityDto getFacilityById(@Parameter(description = "Facility ID") @PathVariable Long id) {
        return facilityService.getFacilityById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update facility", description = "Updates facility details. Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid facility data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    public ParkingFacilityDto updateFacility(@Parameter(description = "Facility ID") @PathVariable Long id, @Valid @RequestBody ParkingFacilityDto facilityDto) {
        return facilityService.updateFacility(id, facilityDto);
    }

    @GetMapping("/{facilityId}/floor-summary")
    @Operation(summary = "Get facility floor spot summary", description = "Retrieves spot type counts grouped by floor for a facility")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Floor spot summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FloorSpotTypeCounts.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<FloorSpotTypeCounts> getFacilityFloorSummary(
            @Parameter(description = "Facility ID", example = "1") @PathVariable Long facilityId) {
        return facilityService.getFloorSpotSummary(facilityId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete facility", description = "Deletes a facility. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Facility deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    public void deleteFacility(@Parameter(description = "Facility ID") @PathVariable Long id) {
        facilityService.deleteFacility(id);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby facilities", description = "Finds parking facilities within specified radius with real-time availability. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nearby facilities retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates or radius")
    })
    public NearbyFacilitiesDto findNearbyFacilities(
            @Parameter(description = "Latitude (-90 to 90)") @RequestParam @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") BigDecimal latitude,
            @Parameter(description = "Longitude (-180 to 180)") @RequestParam @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") BigDecimal longitude,
            @Parameter(description = "Search radius in meters (100-50000)") @RequestParam(defaultValue = "1000") @Min(100) @Max(50000) Integer radiusMeters) {
        return facilityService.findNearbyFacilities(latitude, longitude, radiusMeters);
    }
}
