package com.memplas.parking.feature.facility.repository;

import com.memplas.parking.feature.facility.model.DailyFacilityStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyFacilityStatsRepository extends JpaRepository<DailyFacilityStats, Long>, JpaSpecificationExecutor<DailyFacilityStats> {
    Optional<DailyFacilityStats> findByFacilityIdAndDate(Long facilityId, LocalDate date);

    List<DailyFacilityStats> findByFacilityId(Long facilityId);

    List<DailyFacilityStats> findByDate(LocalDate date);

    List<DailyFacilityStats> findByFacilityIdAndDateBetween(Long facilityId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(dfs.totalRevenue) FROM DailyFacilityStats dfs WHERE dfs.date >= :startDate AND dfs.date <= :endDate")
    BigDecimal getTotalRevenueInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(dfs.totalSessions) FROM DailyFacilityStats dfs WHERE dfs.facility.id = :facilityId AND dfs.date >= :startDate AND dfs.date <= :endDate")
    Long getTotalSessionsForFacilityInPeriod(@Param("facilityId") Long facilityId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT dfs FROM DailyFacilityStats dfs 
            WHERE dfs.date >= :startDate AND dfs.date <= :endDate 
            ORDER BY dfs.totalRevenue DESC
            """)
    List<DailyFacilityStats> findTopRevenueGeneratingFacilities(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(dfs.peakOccupancyRate) FROM DailyFacilityStats dfs WHERE dfs.facility.id = :facilityId AND dfs.date >= :startDate AND dfs.date <= :endDate")
    BigDecimal getAverageOccupancyForFacilityInPeriod(@Param("facilityId") Long facilityId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
