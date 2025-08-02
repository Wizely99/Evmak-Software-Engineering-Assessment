package com.memplas.parking.feature.vehicle.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.vehicle.dto.VehicleDto;
import com.memplas.parking.feature.vehicle.mapper.VehicleMapper;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import com.memplas.parking.feature.vehicle.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper,
                          AuthenticatedUserProvider authenticatedUserProvider) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public VehicleDto createVehicle(VehicleDto vehicleDto) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Ensure user can only create vehicles for themselves (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId2.equals(vehicleDto.userId())) {
            throw new SecurityException("Access denied: Cannot create vehicle for other users");
        }

        // Performance: Single query to reset primary vehicle if setting new primary
        if (Boolean.TRUE.equals(vehicleDto.isPrimary())) {
            vehicleRepository.resetPrimaryVehicleForUser(vehicleDto.userId());
        }

        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(savedVehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getCurrentUserVehicles() {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();
        return getUserVehicles(currentUser.userId2);
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getUserVehicles(Long userId) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only view their own vehicles (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId2.equals(userId)) {
            throw new SecurityException("Access denied: Cannot view other user's vehicles");
        }

        List<Vehicle> vehicles = vehicleRepository.findByUserId(userId);
        return vehicles.stream().map(vehicleMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only view their own vehicles (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId2.equals(vehicle.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot view other user's vehicle");
        }

        return vehicleMapper.toDto(vehicle);
    }

    public VehicleDto updateVehicle(Long id, VehicleDto vehicleDto) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only update their own vehicles (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId2.equals(existingVehicle.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot update other user's vehicle");
        }

        Vehicle updatedVehicle = vehicleMapper.toEntity(vehicleDto);
        updatedVehicle.setId(id);
        updatedVehicle.setCreatedAt(existingVehicle.getCreatedAt());

        Vehicle savedVehicle = vehicleRepository.save(updatedVehicle);
        return vehicleMapper.toDto(savedVehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

    private boolean hasRole(String role) {
        return authenticatedUserProvider.getCurrentKeycloakUser().roles().contains(role);
    }
}
