package com.memplas.parking.feature.paymentprocessing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentCallbackDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentIntegrationMapper;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.model.Payment;
import com.memplas.parking.feature.paymentprocessing.model.PaymentStatus;
import com.memplas.parking.feature.paymentprocessing.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Component
public class MobileMoneyPaymentStrategy implements PaymentStrategy {
    private static final Set<String> SUPPORTED_PROVIDERS =
            Set.of("VodaCom", "AirtelMoney", "MixByYass", "Halotel");

    private final RestTemplate restTemplate;

    private final PaymentRepository paymentRepository;

    private final PaymentIntegrationMapper integrationMapper;

    private final ObjectMapper objectMapper;

    @Value("${payment.xpayment.url}")
    private String paymentProviderUrl;

    @Value("${payment.xpayment.api-key}")
    private String apiKey;

    public MobileMoneyPaymentStrategy(
            RestTemplate restTemplate,
            PaymentRepository paymentRepository,
            PaymentIntegrationMapper integrationMapper,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.paymentRepository = paymentRepository;
        this.integrationMapper = integrationMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Payment processPayment(PaymentRequestDto request, String sessionReference) {
        Map<String, Object> paymentRequest = integrationMapper.toCardPaymentRequest(request);
        paymentRequest.put("API_KEY", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paymentProviderUrl + "/v1/mobile-money-payment", entity, Map.class);

            Payment payment = new Payment();
            payment.setPaymentReference(request.paymentReference());
            payment.setAmount(request.amount());
            payment.setCurrency(request.currency());
            payment.setPaymentMethod(request.paymentMethod());
            payment.setPaymentProvider(request.paymentProvider());
            payment.setStatus(PaymentStatus.PENDING);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                payment.setExternalPaymentId((String) responseBody.get("payment_id"));
                payment.setCallbackData(objectMapper.valueToTree(responseBody));
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Mobile money request failed");
            }

            return paymentRepository.save(payment);

        } catch (Exception e) {
            Payment failedPayment = new Payment();
            failedPayment.setPaymentReference(request.paymentReference());
            failedPayment.setStatus(PaymentStatus.FAILED);
            failedPayment.setFailureReason("Mobile money processing error: " + e.getMessage());

            return paymentRepository.save(failedPayment);
        }
    }

    @Override
    public void handleCallback(PaymentCallbackDto callback) {
        Payment payment = paymentRepository.findByPaymentReference(callback.reference())
                .orElse(null);

        if (payment != null) {
            if ("success".equalsIgnoreCase(callback.status())) {
                payment.setStatus(PaymentStatus.COMPLETED);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(callback.message());
            }
            payment.setCallbackData(objectMapper.valueToTree(callback));
            paymentRepository.save(payment);
        }
    }

    @Override
    public boolean supports(String paymentProvider) {
        return SUPPORTED_PROVIDERS.contains(paymentProvider);
    }
}
