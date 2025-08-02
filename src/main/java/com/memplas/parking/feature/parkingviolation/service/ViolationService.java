package com.memplas.parking.feature.parkingviolation.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.parkingviolation.dto.ViolationDto;
import com.memplas.parking.feature.parkingviolation.mapper.ViolationMapper;
import com.memplas.parking.feature.parkingviolation.model.Violation;
import com.memplas.parking.feature.parkingviolation.repository.ViolationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ViolationService {
    private final ViolationRepository violationRepository;

    private final ViolationMapper violationMapper;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ViolationService(ViolationRepository violationRepository,
                            ViolationMapper violationMapper,
                            AuthenticatedUserProvider authenticatedUserProvider) {
        this.violationRepository = violationRepository;
        this.violationMapper = violationMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PreAuthorize("hasRole('ENFORCEMENT_OFFICER') or hasRole('ADMIN')")
    public ViolationDto createViolation(ViolationDto violationDto) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        Violation violation = violationMapper.toEntity(violationDto);
        // Set reported by current officer
        violation.setReportedBy(currentUser.fullName());

        Violation savedViolation = violationRepository.save(violation);
        return violationMapper.toDto(savedViolation);
    }

    @Transactional(readOnly = true)
    public List<ViolationDto> getCurrentUserViolations() {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Get violations for user's vehicles
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(v -> v.getVehicle().getUser().getId().equals(currentUser.userId()))
                .toList();

        return violations.stream().map(violationMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ViolationDto getViolationById(Long id) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Violation not found with id: " + id));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can view violations for their vehicles, officers/admins can view all
        if (!hasRole("ADMIN") && !hasRole("ENFORCEMENT_OFFICER") &&
                !currentUser.userId().equals(violation.getVehicle().getUser().getId())) {
            throw new SecurityException("Access denied: Cannot view other user's parkingviolation");
        }

        return violationMapper.toDto(violation);
    }

    @PreAuthorize("hasRole('ENFORCEMENT_OFFICER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ViolationDto> getAllViolations() {
        List<Violation> violations = violationRepository.findAll();
        return violations.stream().map(violationMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ENFORCEMENT_OFFICER') or hasRole('ADMIN')")
    public ViolationDto updateViolationStatus(Long id, ViolationDto violationDto) {
        Violation existingViolation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Violation not found with id: " + id));

        Violation updatedViolation = violationMapper.toEntity(violationDto);
        updatedViolation.setId(id);
        updatedViolation.setCreatedAt(existingViolation.getCreatedAt());

        Violation savedViolation = violationRepository.save(updatedViolation);
        return violationMapper.toDto(savedViolation);
    }

    private boolean hasRole(String role) {
        return authenticatedUserProvider.getCurrentKeycloakUser().roles().contains(role);
    }
}
