package com.memplas.parking.feature.pricing.repository;

import com.memplas.parking.feature.pricing.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long>, JpaSpecificationExecutor<PricingRule> {
    List<PricingRule> findByFacilityId(Long facilityId);

    List<PricingRule> findByFacilityIdAndIsActiveTrue(Long facilityId);

    @Query("""
            SELECT pr FROM PricingRule pr 
            WHERE pr.facility.id = :facilityId 
            AND pr.isActive = true 
            AND pr.effectiveFrom <= :currentDate 
            AND (pr.effectiveUntil IS NULL OR pr.effectiveUntil >= :currentDate)
            AND (pr.timeOfDayStart IS NULL OR pr.timeOfDayEnd IS NULL 
                 OR :currentTime BETWEEN pr.timeOfDayStart AND pr.timeOfDayEnd)
            ORDER BY pr.effectiveFrom DESC, pr.timeOfDayStart
            """)
    List<PricingRule> findApplicableRules(
            @Param("facilityId") Long facilityId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    @Query("SELECT pr FROM PricingRule pr WHERE pr.effectiveUntil < :currentDate AND pr.isActive = true")
    List<PricingRule> findExpiredActiveRules(@Param("currentDate") LocalDate currentDate);
}
