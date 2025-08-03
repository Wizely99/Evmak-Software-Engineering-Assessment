package com.memplas.parking.feature.paymentprocessing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memplas.parking.feature.paymentprocessing.dto.MobileMoneyPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MobileMoneyPaymentService {
    private final RestTemplate restTemplate;

    @Value("${payment.mobile.url}")
    private String mobilePaymentUrl;

    public MobileMoneyPaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponseDto pay(MobileMoneyPaymentRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MobileMoneyPaymentRequestDto> entity = new HttpEntity<>(request, headers);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return restTemplate.postForObject(mobilePaymentUrl, entity, PaymentResponseDto.class);
    }

}
