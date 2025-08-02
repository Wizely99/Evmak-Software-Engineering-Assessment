package com.memplas.parking.feature.book.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.account.user.model.UserStatus;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Keycloak user ID is required")
    private String keycloakUserId;

    @Column(nullable = false, unique = true)
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(unique = true, length = 20)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid")
    private String phone;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Vehicle> vehicles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParkingSession> parkingSessions = new HashSet<>();

    // Constructors
    public User() {}

    public User(Long id) {
        super(id);
    }

    // Getters and Setters
    public String getKeycloakUserId() {return keycloakUserId;}

    public void setKeycloakUserId(String keycloakUserId) {this.keycloakUserId = keycloakUserId;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public UserStatus getStatus() {return status;}

    public void setStatus(UserStatus status) {this.status = status;}

    public Set<Vehicle> getVehicles() {return vehicles;}

    public void setVehicles(Set<Vehicle> vehicles) {this.vehicles = vehicles;}

    public Set<ParkingSession> getParkingSessions() {return parkingSessions;}

    public void setParkingSessions(Set<ParkingSession> parkingSessions) {this.parkingSessions = parkingSessions;}
}


// SpotStatus.java


