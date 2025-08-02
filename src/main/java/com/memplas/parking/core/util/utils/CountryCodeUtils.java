package com.memplas.parking.core.util.utils;
import java.util.Arrays;
import java.util.Locale;

public class CountryCodeUtils {


    public static String getCountryName(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return null;
        }

        countryCode = countryCode.toUpperCase();
        if (!Arrays.asList(Locale.getISOCountries()).contains(countryCode)) {
            return null;
        }

        return new Locale.Builder()
                .setRegion(countryCode)
                .build()
                .getDisplayCountry(Locale.ENGLISH);
    }

    public static String getCountryCode(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(Locale.getISOCountries())
                .filter(code -> new Locale.Builder()
                        .setRegion(code)
                        .build()
                        .getDisplayCountry(Locale.ENGLISH)
                        .equalsIgnoreCase(countryName))
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args) {
        // Test various scenarios
        System.out.println("Country name for US: " + getCountryName("US")); // United States
        System.out.println("Country name for us: " + getCountryName("us")); // United States
        System.out.println("Country name for invalid: " + getCountryName("XX")); // null

        System.out.println("Country code for United States: " + getCountryCode("United States")); // US
        System.out.println("Country code for UNITED STATES: " + getCountryCode("UNITED STATES")); // US
        System.out.println("Country code for invalid: " + getCountryCode("Invalid Country")); // null
    }
}