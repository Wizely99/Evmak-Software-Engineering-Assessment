package com.memplas.parking.feature.pricing.service;

import java.time.LocalDateTime;

public interface EventService {
    boolean hasActiveEvent(Long facilityId, LocalDateTime dateTime);
}
