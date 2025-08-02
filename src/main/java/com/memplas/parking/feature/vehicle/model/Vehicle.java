package com.memplas.parking.feature.vehicle.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingviolation.model.Violation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @Column(nullable = false, unique = true, length = 20)
    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate must not exceed 20 characters")
    private String licensePlate;

    @Column(length = 50)
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;

    @Column(length = 50)
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @Column(length = 30)
    @Size(max = 30, message = "Color must not exceed 30 characters")
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType = VehicleType.CAR;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParkingSession> parkingSessions = new HashSet<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Violation> violations = new HashSet<>();

    // Constructors
    public Vehicle() {}

    public Vehicle(Long id) {
        super(id);
    }

    // Getters and Setters
    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public String getLicensePlate() {return licensePlate;}

    public void setLicensePlate(String licensePlate) {this.licensePlate = licensePlate;}

    public String getMake() {return make;}

    public void setMake(String make) {this.make = make;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

    public String getColor() {return color;}

    public void setColor(String color) {this.color = color;}

    public VehicleType getVehicleType() {return vehicleType;}

    public void setVehicleType(VehicleType vehicleType) {this.vehicleType = vehicleType;}

    public Boolean getIsPrimary() {return isPrimary;}

    public void setIsPrimary(Boolean isPrimary) {this.isPrimary = isPrimary;}

    public Set<ParkingSession> getParkingSessions() {return parkingSessions;}

    public void setParkingSessions(Set<ParkingSession> parkingSessions) {this.parkingSessions = parkingSessions;}

    public Set<Violation> getViolations() {return violations;}

    public void setViolations(Set<Violation> violations) {this.violations = violations;}
}
