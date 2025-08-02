package com.memplas.parking.feature.parkingsession.controller;

import com.memplas.parking.feature.parkingsession.dto.ParkingSessionDto;
import com.memplas.parking.feature.parkingsession.dto.ReservationRequestDto;
import com.memplas.parking.feature.parkingsession.dto.ReservationResponseDto;
import com.memplas.parking.feature.parkingsession.service.ParkingSessionService;
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
@RequestMapping("/api/v1/parking")
@Validated
@Tag(name = "Parking Sessions", description = "Parking reservation and session management")
@SecurityRequirement(name = "bearerAuth")
public class ParkingSessionController {
    private final ParkingSessionService sessionService;

    public ParkingSessionController(ParkingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reserve parking spot", description = "Reserves a parking spot for 15 minutes. Handles high-concurrency with retry mechanism.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Spot reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reservation data"),
            @ApiResponse(responseCode = "409", description = "Spot not available or already reserved"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ReservationResponseDto reserveSpot(@Valid @RequestBody ReservationRequestDto request) {
        return sessionService.reserveSpot(request);
    }

    @PostMapping("/confirm/{sessionReference}")
    @Operation(summary = "Confirm arrival", description = "Confirms arrival and activates parking session. Must be called within 15 minutes of reservation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arrival confirmed, session activated"),
            @ApiResponse(responseCode = "403", description = "Access denied - not session owner"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "409", description = "Session not in reserved state")
    })
    public ParkingSessionDto confirmArrival(@Parameter(description = "Session reference (PACK-XXXXXX)") @PathVariable String sessionReference) {
        return sessionService.confirmArrival(sessionReference);
    }

    @PostMapping("/end/{sessionReference}")
    @Operation(summary = "End parking session", description = "Ends active parking session and releases spot. Calculates final amount based on actual duration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session ended successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - not session owner"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "409", description = "Session not active")
    })
    public ParkingSessionDto endSession(@Parameter(description = "Session reference (PACK-XXXXXX)") @PathVariable String sessionReference) {
        return sessionService.endSession(sessionReference);
    }

    @GetMapping("/{sessionReference}")
    @Operation(summary = "Get session details", description = "Retrieves parking session details by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ParkingSessionDto getSessionByReference(@Parameter(description = "Session reference (PACK-XXXXXX)") @PathVariable String sessionReference) {
        return sessionService.getSessionByReference(sessionReference);
    }

    @GetMapping("/me/active")
    @Operation(summary = "Get current user active sessions", description = "Retrieves active parking sessions for current user")
    @ApiResponse(responseCode = "200", description = "Active sessions retrieved successfully")
    public List<ParkingSessionDto> getCurrentUserActiveSessions() {
        return sessionService.getCurrentUserActiveSessions();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user sessions", description = "Retrieves all sessions for specified user. Users can view own sessions, ADMINs can view any.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User sessions retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<ParkingSessionDto> getUserSessions(@Parameter(description = "User ID") @PathVariable Long userId) {
        return sessionService.getUserSessions(userId);
    }
}
