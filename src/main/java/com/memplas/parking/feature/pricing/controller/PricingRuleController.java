package com.memplas.parking.feature.pricing.controller;

import com.memplas.parking.feature.pricing.dto.PricingRuleDto;
import com.memplas.parking.feature.pricing.service.PricingRuleService;
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
@RequestMapping("/api/v1/pricing-rules")
@Validated
@Tag(name = "Pricing Rules", description = "Dynamic pricing rule management")
public class PricingRuleController {
    private final PricingRuleService pricingRuleService;

    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create pricing rule", description = "Creates new pricing rule with time-based and demand-based pricing. Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pricing rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pricing rule data"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or FACILITY_MANAGER role required")
    })
    public PricingRuleDto createPricingRule(@Valid @RequestBody PricingRuleDto pricingRuleDto) {
        return pricingRuleService.createPricingRule(pricingRuleDto);
    }

    @GetMapping("/facility/{facilityId}")
    @Operation(summary = "Get facility pricing rules", description = "Retrieves active pricing rules for facility. Public endpoint for transparency.")
    @ApiResponse(responseCode = "200", description = "Pricing rules retrieved successfully")
    public List<PricingRuleDto> getFacilityPricingRules(@Parameter(description = "Facility ID") @PathVariable Long facilityId) {
        return pricingRuleService.getFacilityPricingRules(facilityId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pricing rule", description = "Updates existing pricing rule. Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pricing rule updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pricing rule data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Pricing rule not found")
    })
    public PricingRuleDto updatePricingRule(@Parameter(description = "Pricing rule ID") @PathVariable Long id, @Valid @RequestBody PricingRuleDto pricingRuleDto) {
        return pricingRuleService.updatePricingRule(id, pricingRuleDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete pricing rule", description = "Soft deletes pricing rule (sets inactive). Requires ADMIN or FACILITY_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pricing rule deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Pricing rule not found")
    })
    public void deletePricingRule(@Parameter(description = "Pricing rule ID") @PathVariable Long id) {
        pricingRuleService.deletePricingRule(id);
    }
}
