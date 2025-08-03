package com.memplas.parking.feature.paymentprocessing.dto;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//
//@Mapper(
//        componentModel = MappingConstants.ComponentModel.SPRING,
//        unmappedTargetPolicy = ReportingPolicy.WARN
//)
//public interface PaymentIntegrationMapper {
//    // Map to X-PAYMENT-PROVIDER card payment request
//    @Mapping(target = "REFERENCE", source = "sessionReference")
//    @Mapping(target = "AMOUNT", source = "amount")
//    @Mapping(target = "CURRENCY", source = "currency")
//    @Mapping(target = "PROVIDER", source = "paymentProvider")
//    @Mapping(target = "PAYMENT_FOR", constant = "Parking")
//    @Mapping(target = "PAYER_NAME", source = "payerName")
//    @Mapping(target = "COUNTRY_CODE", constant = "TZ")
//    @Mapping(target = "POSTAL_CODE", constant = "1021")
//    @Mapping(target = "CALLBACK_URL", source = "callbackUrl")
//    @Mapping(target = "CANCEL_URL", source = "cancelUrl")
//    @Mapping(target = "API_KEY", ignore = true)
//    // set from config
//    Map<String, Object> toCardPaymentRequest(PaymentRequestDto dto);
//
//    // Map to X-PAYMENT-PROVIDER mobile money request
//    @Mapping(target = "REFERENCE", source = "sessionReference")
//    @Mapping(target = "AMOUNT", source = "amount")
//    @Mapping(target = "CURRENCY", source = "currency")
//    @Mapping(target = "MNO", source = "paymentProvider")
//    @Mapping(target = "PAYMENT_FOR", constant = "Parking")
//    @Mapping(target = "PAYER_NAME", source = "payerName")
//    @Mapping(target = "CALLBACK_URL", source = "callbackUrl")
//    @Mapping(target = "CANCEL_URL", source = "cancelUrl")
//    @Mapping(target = "API_KEY", ignore = true)
//    // set from config
//    Map<String, Object> toMobileMoneyRequest(PaymentRequestDto dto);
//}
@Component
public class PaymentIntegrationMapper {
    public Map<String, Object> toCardPaymentRequest(PaymentRequestDto dto) {
        Map<String, Object> request = new HashMap<>();
        request.put("reference", dto.sessionReference());
        request.put("amount", dto.amount().toString());
        request.put("currency", dto.currency());
        request.put("provider", dto.paymentProvider());
        request.put("paymentFor", "Parking");
        request.put("payerName", dto.payerName());
        request.put("countryCode", "TZ");
        request.put("postalCode", "1021");
        return request;
    }
}