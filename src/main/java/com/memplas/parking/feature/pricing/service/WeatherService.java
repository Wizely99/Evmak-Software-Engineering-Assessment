package com.memplas.parking.feature.pricing.service;

import com.memplas.parking.feature.pricing.model.WeatherCondition;

// Supporting services (interfaces)
public interface WeatherService {
    WeatherCondition getCurrentWeather(Long facilityId);
}
