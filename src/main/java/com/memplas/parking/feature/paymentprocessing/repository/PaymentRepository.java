package com.memplas.parking.feature.paymentprocessing.repository;

import com.memplas.parking.feature.paymentprocessing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Boolean existsBySessionId(Long sessionId);

}
