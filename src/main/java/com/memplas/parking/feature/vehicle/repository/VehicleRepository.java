package com.memplas.parking.feature.vehicle.repository;

import com.memplas.parking.feature.vehicle.model.Vehicle;
import com.memplas.parking.feature.vehicle.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    List<Vehicle> findByUserId(Long userId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByUserIdAndIsPrimaryTrue(Long userId);

    List<Vehicle> findByVehicleType(VehicleType vehicleType);

    boolean existsByLicensePlate(String licensePlate);

    @Modifying
    @Query("UPDATE Vehicle v SET v.isPrimary = false WHERE v.user.id = :userId")
    void resetPrimaryVehicleForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.vehicleType = :type")
    long countByVehicleType(@Param("type") VehicleType type);
}
