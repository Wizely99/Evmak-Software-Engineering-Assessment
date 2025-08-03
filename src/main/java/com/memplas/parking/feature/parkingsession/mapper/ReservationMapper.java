package com.memplas.parking.feature.parkingsession.mapper;

import com.memplas.parking.feature.parkingsession.dto.ReservationResponseDto;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ReservationMapper {
    @Mapping(target = "sessionReference", source = "sessionReference")
    @Mapping(target = "spotId", source = "spot.id")
    @Mapping(target = "spotNumber", source = "spot.spotNumber")
    @Mapping(target = "facilityName", source = "spot.facility.name")
    @Mapping(target = "reservationExpiry", source = "spot.reservationExpiry")
    @Mapping(target = "estimatedAmount", source = "totalAmount")
    ReservationResponseDto toReservationResponse(ParkingSession session);
}
