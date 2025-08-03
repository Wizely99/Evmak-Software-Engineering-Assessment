package com.memplas.parking.feature.pricing.service;

import com.memplas.parking.feature.pricing.model.WeatherCondition;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MockWeatherService implements WeatherService {
    private final Random random = new Random();

    @Override
    public WeatherCondition getCurrentWeather(Long facilityId) {
        // Simple mock: 70% clear, 20% rain, 5% snow, 5% extreme heat
        int chance = random.nextInt(100);
        if (chance < 70) return WeatherCondition.CLEAR;
        if (chance < 90) return WeatherCondition.RAIN;
        if (chance < 95) return WeatherCondition.SNOW;
        return WeatherCondition.EXTREME_HEAT;
    }
}

