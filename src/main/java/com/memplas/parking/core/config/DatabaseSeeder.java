package com.memplas.parking.core.config;

import com.memplas.parking.feature.account.user.model.UserStatus;
import com.memplas.parking.feature.account.user.repository.UserRepository;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.facility.model.FacilityStatus;
import com.memplas.parking.feature.facility.model.FacilityType;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.facility.repository.ParkingFacilityRepository;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import com.memplas.parking.feature.pricing.model.PricingRule;
import com.memplas.parking.feature.pricing.repository.PricingRuleRepository;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import com.memplas.parking.feature.vehicle.model.VehicleType;
import com.memplas.parking.feature.vehicle.repository.VehicleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseSeeder {
    private final UserRepository userRepository;

    private final VehicleRepository vehicleRepository;

    private final ParkingFacilityRepository facilityRepository;

    private final ParkingSpotRepository spotRepository;

    private final PricingRuleRepository pricingRuleRepository;

    public DatabaseSeeder(UserRepository userRepository, VehicleRepository vehicleRepository,
                          ParkingFacilityRepository facilityRepository, ParkingSpotRepository spotRepository,
                          PricingRuleRepository pricingRuleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.facilityRepository = facilityRepository;
        this.spotRepository = spotRepository;
        this.pricingRuleRepository = pricingRuleRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener(ApplicationReadyEvent.class)

    public void seedDatabase() {
        if (facilityRepository.count() == 0) {
            System.out.println("🌱 Seeding parking database...");
            seedUsers();
            seedVehicles();
            seedFacilities();
            seedParkingSpots();
            seedPricingRules();
//            seedAvailabilityCache();
            System.out.println("✅ Database seeding completed!");
        } else {
            System.out.println("📦 Database already contains data, skipping seeding.");
        }
    }

    private void seedUsers() {
        List<User> users = List.of(
                createUser(UUID.randomUUID().toString(), "john.doe@example.com", "+255712345678", "John", "Doe"),
                createUser(UUID.randomUUID().toString(), "jane.smith@example.com", "+255723456789", "Jane", "Smith"),
                createUser(UUID.randomUUID().toString(), "admin@parkingsystem.com", "+255734567890", "Admin", "User"),
                createUser(UUID.randomUUID().toString(), "officer@parkingsystem.com", "+255745678901", "Enforcement", "Officer")
        );
        userRepository.saveAll(users);
        System.out.println("👥 Seeded " + users.size() + " users");
    }

    private void seedVehicles() {
        List<User> users = userRepository.findAll();
        List<Vehicle> vehicles = List.of(
                createVehicle(users.get(0), "T123ABC", "Toyota", "Corolla", "Silver", VehicleType.CAR, true),
                createVehicle(users.get(1), "T456DEF", "Honda", "Civic", "Blue", VehicleType.CAR, true),
                createVehicle(users.get(1), "T789GHI", "Yamaha", "R15", "Red", VehicleType.MOTORCYCLE, false),
                createVehicle(users.get(0), "T321JKL", "Nissan", "Navara", "White", VehicleType.TRUCK, false)
        );
        vehicleRepository.saveAll(vehicles);
        System.out.println("🚗 Seeded " + vehicles.size() + " vehicles");
    }

    private void seedFacilities() {
        List<ParkingFacility> facilities = List.of(
                createFacility("Mlimani City Mall Garage", FacilityType.GARAGE,
                        "Mlimani City Mall, Sam Nujoma Road",
                        -6.7539, 39.2155, 200, 250),
                createFacility("Kariakoo Market Garage", FacilityType.GARAGE,
                        "Kariakoo Market, Uhuru Street",
                        -6.8235, 39.2638, 150, 220),
                createFacility("City Centre Street Zone", FacilityType.STREET_ZONE,
                        "Samora Avenue, City Centre",
                        -6.8167, 39.2833, 50, null),
                createFacility("University of Dar es Salaam", FacilityType.GARAGE,
                        "UDSM Main Campus",
                        -6.7756, 39.2467, 300, 200),
                createFacility("Julius Nyerere Airport Terminal", FacilityType.GARAGE,
                        "Julius Nyerere International Airport",
                        -6.8781, 39.2026, 400, 300)
        );

        facilityRepository.saveAll(facilities);
        System.out.println("🏢 Seeded " + facilities.size() + " parking facilities");
    }

    private void seedParkingSpots() {
        List<ParkingFacility> facilities = facilityRepository.findAll();
        int totalSpots = 0;

        for (ParkingFacility facility : facilities) {
            List<ParkingSpot> spots = createSpotsForFacility(facility);
            spotRepository.saveAll(spots);
            totalSpots += spots.size();
        }
        System.out.println("🅿️ Seeded " + totalSpots + " parking spots");
    }

    private void seedPricingRules() {
        List<ParkingFacility> facilities = facilityRepository.findAll();

        for (ParkingFacility facility : facilities) {
            List<PricingRule> rules = createPricingRulesForFacility(facility);
            pricingRuleRepository.saveAll(rules);
        }
        System.out.println("💰 Seeded pricing rules for " + facilities.size() + " facilities");
    }

    private User createUser(String keycloakId, String email, String phone, String firstName, String lastName) {
        User user = new User();
        user.setKeycloakUserId(keycloakId);
        user.setEmail(email);
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private Vehicle createVehicle(User user, String licensePlate, String make, String model,
                                  String color, VehicleType type, boolean isPrimary) {
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setVehicleType(type);
        vehicle.setIsPrimary(isPrimary);
        return vehicle;
    }

    private ParkingFacility createFacility(String name, FacilityType type, String address,
                                           Double lat, Double lng, int totalSpots, Integer maxHeight) {
        ParkingFacility facility = new ParkingFacility();
        facility.setName(name);
        facility.setFacilityType(type);
        facility.setAddress(address);
        facility.setLocationLat(lat);
        facility.setLocationLng(lng);
        facility.setTotalSpots(totalSpots);
        facility.setMaxHeightCm(maxHeight);
        facility.setOperatingHoursStart(LocalTime.of(6, 0));
        facility.setOperatingHoursEnd(LocalTime.of(22, 0));
        facility.setStatus(FacilityStatus.ACTIVE);
        return facility;
    }

    private List<ParkingSpot> createSpotsForFacility(ParkingFacility facility) {
        List<ParkingSpot> spots = new java.util.ArrayList<>();

        for (int i = 1; i <= facility.getTotalSpots(); i++) {
            ParkingSpot spot = new ParkingSpot();
            spot.setFacility(facility);
            spot.setSpotNumber(String.format("%s-%03d", getSpotPrefix(facility.getFacilityType()), i));
            spot.setSpotType(determineSpotType(i));
            spot.setFloorLevel(determineFloorLevel(facility.getFacilityType(), i));
            spot.setZone(determineZone(facility.getFacilityType(), i));
            spot.setStatus(SpotStatus.AVAILABLE);
            spots.add(spot);
        }

        return spots;
    }

    private List<PricingRule> createPricingRulesForFacility(ParkingFacility facility) {
        return List.of(
                createPricingRule(facility, "Standard Rate", new BigDecimal("1000.00"), null, null),
                createPricingRule(facility, "Peak Hours", new BigDecimal("1500.00"),
                        LocalTime.of(8, 0), LocalTime.of(18, 0)),
                createPricingRule(facility, "Night Rate", new BigDecimal("800.00"),
                        LocalTime.of(18, 0), LocalTime.of(6, 0))
        );
    }

    private PricingRule createPricingRule(ParkingFacility facility, String name, BigDecimal baseRate,
                                          LocalTime startTime, LocalTime endTime) {
        PricingRule rule = new PricingRule();
        rule.setFacility(facility);
        rule.setRuleName(name);
        rule.setBaseRate(baseRate);
        rule.setCurrency("TZS");
        rule.setTimeOfDayStart(startTime);
        rule.setTimeOfDayEnd(endTime);
        rule.setDayOfWeekMask("1111111"); // All days
        rule.setDemandMultiplier(new BigDecimal("1.5"));
        rule.setOccupancyThreshold(80);
        rule.setMaxDailyRate(new BigDecimal("15000.00"));
        rule.setGracePeriodMinutes(15);
        rule.setIsActive(true);
        rule.setEffectiveFrom(LocalDate.now());
        return rule;
    }

    private String getSpotPrefix(FacilityType type) {
        return type == FacilityType.GARAGE ? "G" : "S";
    }

    private SpotType determineSpotType(int spotNumber) {
        if (spotNumber % 20 == 0) return SpotType.DISABLED;
        if (spotNumber % 15 == 0) return SpotType.ELECTRIC;
        if (spotNumber % 10 == 0) return SpotType.COMPACT;
        return SpotType.REGULAR;
    }

    private Integer determineFloorLevel(FacilityType type, int spotNumber) {
        if (type == FacilityType.STREET_ZONE) return 0;
        return (spotNumber - 1) / 50; // 50 spots per floor
    }

    private String determineZone(FacilityType type, int spotNumber) {
        if (type == FacilityType.STREET_ZONE) return "STREET";
        char zone = (char) ('A' + ((spotNumber - 1) / 25)); // 25 spots per zone
        return String.valueOf(zone);
    }
}