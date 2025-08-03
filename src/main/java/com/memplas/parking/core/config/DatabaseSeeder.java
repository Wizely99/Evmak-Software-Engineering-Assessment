package com.memplas.parking.core.config;

import com.memplas.parking.feature.account.user.model.UserStatus;
import com.memplas.parking.feature.account.user.repository.UserRepository;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.facility.model.FacilityStatus;
import com.memplas.parking.feature.facility.model.FacilityType;
import com.memplas.parking.feature.facility.model.Floor;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.facility.repository.FloorRepository;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseSeeder {
    private final UserRepository userRepository;

    private final VehicleRepository vehicleRepository;

    private final ParkingFacilityRepository facilityRepository;

    private final FloorRepository floorRepository;

    private final ParkingSpotRepository spotRepository;

    private final PricingRuleRepository pricingRuleRepository;

    public DatabaseSeeder(UserRepository userRepository, VehicleRepository vehicleRepository,
                          ParkingFacilityRepository facilityRepository, FloorRepository floorRepository,
                          ParkingSpotRepository spotRepository, PricingRuleRepository pricingRuleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.facilityRepository = facilityRepository;
        this.floorRepository = floorRepository;
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
            seedFloors();
            seedParkingSpots();
            seedPricingRules();
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
                        "Mlimani City Mall, Sam Nujoma Road", -6.7539, 39.2155, 200, 4,
                        List.of("Security", "CCTV", "EV Charging", "Covered")),
                createFacility("Kariakoo Market Garage", FacilityType.GARAGE,
                        "Kariakoo Market, Uhuru Street", -6.8235, 39.2638, 150, 3,
                        List.of("Security", "CCTV", "24/7 Access")),
                createFacility("City Centre Street Zone", FacilityType.STREET_ZONE,
                        "Samora Avenue, City Centre", -6.8167, 39.2833, 50, 1,
                        List.of("Metered", "Time Limited")),
                createFacility("University of Dar es Salaam", FacilityType.GARAGE,
                        "UDSM Main Campus", -6.7756, 39.2467, 300, 5,
                        List.of("Student Discount", "EV Charging", "Security")),
                createFacility("Julius Nyerere Airport Terminal", FacilityType.GARAGE,
                        "Julius Nyerere International Airport", -6.8781, 39.2026, 400, 6,
                        List.of("24/7 Access", "Security", "Shuttle Service", "Long-term"))
        );

        facilityRepository.saveAll(facilities);
        System.out.println("🏢 Seeded " + facilities.size() + " parking facilities");
    }

    private void seedFloors() {
        List<ParkingFacility> facilities = facilityRepository.findAll();
        int totalFloors = 0;

        for (ParkingFacility facility : facilities) {
            List<Floor> floors = createFloorsForFacility(facility);
            floorRepository.saveAll(floors);
            totalFloors += floors.size();
        }
        System.out.println("🏬 Seeded " + totalFloors + " floors");
    }

    private void seedParkingSpots() {
        List<ParkingFacility> facilities = facilityRepository.findAll();
        int totalSpots = 0;

        for (ParkingFacility facility : facilities) {
            List<Floor> floors = floorRepository.findByFacilityOrderByNumber(facility);
            List<ParkingSpot> spots = createSpotsForFacility(facility, floors);
            spotRepository.saveAll(spots);
            totalSpots += spots.size();
        }
        System.out.println("🅿️ Seeded " + totalSpots + " parking spots");
    }

    private List<Floor> createFloorsForFacility(ParkingFacility facility) {
        List<Floor> floors = new ArrayList<>();
        int spotsPerFloor = facility.getTotalSpots() / facility.getTotalFloorCount();
        int spotsPerRow = 10; // 10 spots per row
        int rowsPerFloor = (spotsPerFloor + spotsPerRow - 1) / spotsPerRow; // Ceiling division

        for (int floorNumber = 0; floorNumber < facility.getTotalFloorCount(); floorNumber++) {
            Floor floor = new Floor();
            floor.setFacility(facility);
            floor.setNumber(floorNumber);
            floor.setName(getFloorName(facility.getFacilityType(), floorNumber));
            floor.setColumns(spotsPerRow);
            floor.setRows(rowsPerFloor);
            floor.setAccessible(true);
            floor.setMaxHeight(facility.getFacilityType() == FacilityType.GARAGE ? 2.5 : null);
            floors.add(floor);
        }
        return floors;
    }

    private String getFloorName(FacilityType facilityType, int floorNumber) {
        if (facilityType == FacilityType.STREET_ZONE) {
            return "Street Level";
        }
        if (floorNumber == 0) {
            return "Ground Floor";
        }
        return "Floor " + (floorNumber + 1);
    }

    private List<ParkingSpot> createSpotsForFacility(ParkingFacility facility, List<Floor> floors) {
        List<ParkingSpot> spots = new ArrayList<>();
        int spotsPerFloor = facility.getTotalSpots() / facility.getTotalFloorCount();
        int spotsPerRow = 10;

        int spotIndex = 0;
        for (Floor floor : floors) {
            for (int row = 0; row < floor.getRows(); row++) {
                for (int col = 1; col <= floor.getColumns() && spotIndex < facility.getTotalSpots(); col++) {
                    char rowLetter = (char) ('A' + row);
                    String spotNumber = String.format("%c%d-%d", rowLetter, floor.getNumber() + 1, col);

                    ParkingSpot spot = new ParkingSpot();
                    spot.setFacility(facility);
                    spot.setFloor(floor); // Assign floor entity
                    spot.setSpotNumber(spotNumber);
                    spot.setSpotType(determineSpotType(spotIndex + 1));
                    spot.setFloorLevel(floor.getNumber());
                    spot.setRow(row);
                    spot.setCol(col);
                    spot.setStatus(SpotStatus.AVAILABLE);
                    spots.add(spot);
                    spotIndex++;
                }
            }
        }
        return spots;
    }

    private void seedPricingRules() {
        List<ParkingFacility> facilities = facilityRepository.findAll();

        for (ParkingFacility facility : facilities) {
            PricingRule rule = createPricingRuleForFacility(facility);
            pricingRuleRepository.save(rule);
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
                                           Double lat, Double lng, int totalSpots, int floorCount, List<String> amenities) {
        ParkingFacility facility = new ParkingFacility();
        facility.setName(name);
        facility.setFacilityType(type);
        facility.setAddress(address);
        facility.setLocationLat(lat);
        facility.setLocationLng(lng);
        facility.setTotalSpots(totalSpots);
        facility.setTotalFloorCount(floorCount);
        facility.setOperatingHoursStart(LocalTime.of(6, 0));
        facility.setOperatingHoursEnd(LocalTime.of(22, 0));
        facility.setStatus(FacilityStatus.ACTIVE);
        facility.setAmenities(amenities);
        return facility;
    }

    private List<ParkingSpot> createSpotsForFacility(ParkingFacility facility) {
        List<ParkingSpot> spots = new ArrayList<>();
        int spotsPerFloor = facility.getTotalSpots() / facility.getTotalFloorCount();
        int spotsPerRow = 10; // 10 spots per row

        int spotIndex = 0;
        for (int floor = 0; floor < facility.getTotalFloorCount(); floor++) {
            for (int row = 0; row < (spotsPerFloor / spotsPerRow); row++) {
                for (int col = 1; col <= spotsPerRow && spotIndex < facility.getTotalSpots(); col++) {
                    char rowLetter = (char) ('A' + row);
                    String spotNumber = String.format("%c%d-%d", rowLetter, floor + 1, col);

                    ParkingSpot spot = new ParkingSpot();
                    spot.setFacility(facility);
                    spot.setSpotNumber(spotNumber);
                    spot.setSpotType(determineSpotType(spotIndex + 1));
                    spot.setFloorLevel(floor);
                    spot.setRow(row);
                    spot.setCol(col);
                    spot.setStatus(SpotStatus.AVAILABLE);
                    spots.add(spot);
                    spotIndex++;
                }
            }
        }
        return spots;
    }

    private PricingRule createPricingRuleForFacility(ParkingFacility facility) {
        PricingRule rule = new PricingRule();
        rule.setFacility(facility);

        // Base rates based on facility type
        if (facility.getFacilityType() == FacilityType.STREET_ZONE) {
            rule.setBaseRate(new BigDecimal("500.00"));
            rule.setVipRate(new BigDecimal("800.00"));
            rule.setEvChargingRate(new BigDecimal("700.00"));
        } else {
            rule.setBaseRate(new BigDecimal("1000.00"));
            rule.setVipRate(new BigDecimal("1500.00"));
            rule.setEvChargingRate(new BigDecimal("1200.00"));
        }

        rule.setCurrency("TZS");
        rule.setPeakHourMultiplier(new BigDecimal("1.50"));
        rule.setOffPeakMultiplier(new BigDecimal("0.80"));
        rule.setFloorDiscountPerLevel(new BigDecimal("5.00"));
        rule.setMaxFloorDiscount(30);
        rule.setDiscountAfterHours(4);
        rule.setHourlyDiscountRate(new BigDecimal("2.00"));
        rule.setMaxDiscountPercentage(new BigDecimal("30.00"));
        rule.setMaxDailyRate(new BigDecimal("15000.00"));
        rule.setDemandMultiplier(new BigDecimal("1.00"));
        rule.setOccupancyThreshold(80);
        rule.setWeatherMultiplierRain(new BigDecimal("1.20"));
        rule.setWeatherMultiplierSnow(new BigDecimal("1.50"));
        rule.setWeatherMultiplierExtremeHeat(new BigDecimal("1.30"));
        rule.setEventMultiplier(new BigDecimal("1.25"));
        rule.setMotorcycleDiscount(new BigDecimal("0.70"));
        rule.setTruckSurcharge(new BigDecimal("1.50"));
        rule.setGracePeriodMinutes(15);

        return rule;
    }

    private SpotType determineSpotType(int spotNumber) {
        if (spotNumber % 20 == 0) return SpotType.DISABLED;
        if (spotNumber % 15 == 0) return SpotType.EV_CHARGING;
        if (spotNumber % 12 == 0) return SpotType.VIP;
        if (spotNumber % 10 == 0) return SpotType.COMPACT;
        return SpotType.REGULAR;
    }
}