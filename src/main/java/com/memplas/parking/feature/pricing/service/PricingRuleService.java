package com.memplas.parking.feature.pricing.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.pricing.dto.PricingRuleDto;
import com.memplas.parking.feature.pricing.mapper.PricingRuleMapper;
import com.memplas.parking.feature.pricing.model.PricingRule;
import com.memplas.parking.feature.pricing.repository.PricingRuleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PricingRuleService {
    private final PricingRuleRepository pricingRuleRepository;

    private final PricingRuleMapper pricingRuleMapper;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public PricingRuleService(PricingRuleRepository pricingRuleRepository,
                              PricingRuleMapper pricingRuleMapper,
                              AuthenticatedUserProvider authenticatedUserProvider) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.pricingRuleMapper = pricingRuleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public PricingRuleDto createPricingRule(PricingRuleDto pricingRuleDto) {
        PricingRule pricingRule = pricingRuleMapper.toEntity(pricingRuleDto);
        PricingRule savedRule = pricingRuleRepository.save(pricingRule);
        return pricingRuleMapper.toDto(savedRule);
    }

    // Public: Anyone can view pricing rules for transparency
    @Transactional(readOnly = true)
    public List<PricingRuleDto> getFacilityPricingRules(Long facilityId) {
        List<PricingRule> rules = pricingRuleRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        return rules.stream().map(pricingRuleMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public PricingRuleDto updatePricingRule(Long id, PricingRuleDto pricingRuleDto) {
        PricingRule existingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pricing rule not found with id: " + id));

        PricingRule updatedRule = pricingRuleMapper.toEntity(pricingRuleDto);
        updatedRule.setId(id);
        updatedRule.setCreatedAt(existingRule.getCreatedAt());

        PricingRule savedRule = pricingRuleRepository.save(updatedRule);
        return pricingRuleMapper.toDto(savedRule);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public void deletePricingRule(Long id) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pricing rule not found with id: " + id));

        // Soft delete by setting inactive
        rule.setIsActive(false);
        pricingRuleRepository.save(rule);
    }
}
