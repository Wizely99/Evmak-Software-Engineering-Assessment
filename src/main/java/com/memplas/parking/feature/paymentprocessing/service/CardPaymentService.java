package com.memplas.parking.feature.paymentprocessing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memplas.parking.feature.paymentprocessing.dto.CardPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CardPaymentService {
    private final RestTemplate restTemplate;

    @Value("${payment.card.url}")
    private String cardPaymentUrl;

    public CardPaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponseDto pay(CardPaymentRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CardPaymentRequestDto> entity = new HttpEntity<>(request, headers);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("entity.getBody()");
        System.out.println(entity.getBody());
        try {
            System.out.println(mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return restTemplate.postForObject(cardPaymentUrl, entity, PaymentResponseDto.class);
    }
}
