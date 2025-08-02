package com.memplas.parking.feature.parkingviolation.controller;

import com.memplas.parking.feature.parkingviolation.dto.ViolationDto;
import com.memplas.parking.feature.parkingviolation.service.ViolationService;
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
@RequestMapping("/api/v1/violations")
@Validated
@Tag(name = "Violations", description = "Parking parkingviolation management")
@SecurityRequirement(name = "bearerAuth")
public class ViolationController {
    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create parkingviolation", description = "Records new parking parkingviolation. Requires ENFORCEMENT_OFFICER or ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Violation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parkingviolation data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ENFORCEMENT_OFFICER or ADMIN role required")
    })
    public ViolationDto createViolation(@Valid @RequestBody ViolationDto violationDto) {
        return violationService.createViolation(violationDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user violations", description = "Retrieves violations for current user's vehicles")
    @ApiResponse(responseCode = "200", description = "User violations retrieved successfully")
    public List<ViolationDto> getCurrentUserViolations() {
        return violationService.getCurrentUserViolations();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parkingviolation by ID", description = "Retrieves parkingviolation details. Users can view own violations, officers/admins can view any.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Violation retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Violation not found")
    })
    public ViolationDto getViolationById(@Parameter(description = "Violation ID") @PathVariable Long id) {
        return violationService.getViolationById(id);
    }

    @GetMapping
    @Operation(summary = "Get all violations", description = "Retrieves all violations. Requires ENFORCEMENT_OFFICER or ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All violations retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ENFORCEMENT_OFFICER or ADMIN role required")
    })
    public List<ViolationDto> getAllViolations() {
        return violationService.getAllViolations();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parkingviolation status", description = "Updates parkingviolation status (paid, disputed, etc.). Requires ENFORCEMENT_OFFICER or ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Violation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parkingviolation data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Violation not found")
    })
    public ViolationDto updateViolationStatus(@Parameter(description = "Violation ID") @PathVariable Long id, @Valid @RequestBody ViolationDto violationDto) {
        return violationService.updateViolationStatus(id, violationDto);
    }
}
