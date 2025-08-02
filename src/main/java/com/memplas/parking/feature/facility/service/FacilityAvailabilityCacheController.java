package com.memplas.parking.feature.facility.service;

import com.memplas.parking.feature.facility.dto.FacilityAvailabilityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/availability-cache")
@Validated
@Tag(name = "Availability Cache", description = "Real-time availability cache management and monitoring")
public class FacilityAvailabilityCacheController {
    private final FacilityAvailabilityCacheManagementService cacheManagementService;

    public FacilityAvailabilityCacheController(FacilityAvailabilityCacheManagementService cacheManagementService) {
        this.cacheManagementService = cacheManagementService;
    }

    @GetMapping("/{facilityId}")
    @Operation(summary = "Get facility cache", description = "Retrieves current availability cache for specific facility. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Facility cache retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Facility cache not found")
    })
    public FacilityAvailabilityDto getFacilityCacheById(@Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        return cacheManagementService.getFacilityCacheById(facilityId);
    }

    @GetMapping
    @Operation(summary = "Get all facility caches", description = "Retrieves availability cache for all facilities. Public endpoint.")
    @ApiResponse(responseCode = "200", description = "All facility caches retrieved successfully")
    public List<FacilityAvailabilityDto> getAllFacilityCaches() {
        return cacheManagementService.getAllFacilityCaches();
    }

    @GetMapping("/available")
    @Operation(summary = "Get facilities with availability", description = "Retrieves facilities that have available spots. Public endpoint for quick availability check.")
    @ApiResponse(responseCode = "200", description = "Available facilities retrieved successfully")
    public List<FacilityAvailabilityDto> getFacilitiesWithAvailability() {
        return cacheManagementService.getFacilitiesWithAvailability();
    }

    @PostMapping("/{facilityId}/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh facility cache", description = "Manually refreshes availability cache for specific facility. Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cache refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or FACILITY_MANAGER role required"),
            @ApiResponse(responseCode = "404", description = "Facility cache not found")
    })
    public FacilityAvailabilityDto refreshFacilityCache(@Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        return cacheManagementService.refreshFacilityCache(facilityId);
    }

    @PostMapping("/refresh-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Refresh all caches", description = "Manually refreshes availability cache for all facilities. Requires ADMIN role. Use with caution on production.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All caches refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public void refreshAllCaches() {
        cacheManagementService.refreshAllCaches();
    }
}
