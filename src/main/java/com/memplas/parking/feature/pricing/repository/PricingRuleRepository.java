package com.memplas.parking.feature.pricing.repository;

import com.memplas.parking.feature.pricing.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    Optional<PricingRule> findByFacilityId(Long facilityId);

    boolean existsByFacilityId(Long facilityId);
}