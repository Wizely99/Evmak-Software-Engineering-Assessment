package com.memplas.parking.core.util.utils.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
  String message() default "Invalid phone number format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String countryCodeField(); // Field name that contains the country code

  String phoneField(); // Field name that contains the phone number
}
