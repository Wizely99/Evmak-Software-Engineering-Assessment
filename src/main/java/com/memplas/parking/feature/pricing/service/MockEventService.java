package com.memplas.parking.feature.pricing.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class MockEventService implements EventService {
    @Override
    public boolean hasActiveEvent(Long facilityId, LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();

        // Mock events during evening hours (6PM-11PM) on weekends
        boolean isWeekend = dateTime.getDayOfWeek().getValue() >= 6;
        boolean isEventTime = time.isAfter(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(23, 0));

        return isWeekend && isEventTime;
    }
}
