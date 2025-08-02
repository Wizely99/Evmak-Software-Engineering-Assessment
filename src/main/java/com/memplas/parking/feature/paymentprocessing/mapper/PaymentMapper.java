package com.memplas.parking.feature.paymentprocessing.mapper;

import com.memplas.parking.feature.paymentprocessing.dto.PaymentDto;
import com.memplas.parking.feature.paymentprocessing.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface PaymentMapper {
    @Mapping(target = "sessionId", source = "session.id")
    PaymentDto toDto(Payment entity);

    @Mapping(target = "session.id", source = "sessionId")
    Payment toEntity(PaymentDto dto);
}
