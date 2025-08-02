package com.memplas.parking.core.util.utils.validation.annotation;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.memplas.parking.core.util.utils.CountryCodeUtils;
import com.memplas.parking.core.util.utils.PhoneNumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, Object> {

  private String countryCodeFieldName;
  private String phoneFieldName;

  @Override
  public void initialize(ValidPhoneNumber constraintAnnotation) {
    this.countryCodeFieldName = constraintAnnotation.countryCodeField();
    this.phoneFieldName = constraintAnnotation.phoneField();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return false; // Null values are invalid
    }
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    // Get all supported country codes
    var countryCodes = phoneNumberUtil.getSupportedRegions();

    try {
      Class<?> clazz = value.getClass();

      // Extract the country from the object
      Field countryField = clazz.getDeclaredField(countryCodeFieldName);
      countryField.setAccessible(true);
      String countryName = (String) countryField.get(value);
      String country = CountryCodeUtils.getCountryCode(countryName);
      var exists = countryCodes.contains(country);
      System.out.println(exists);

      // Extract the phone number from the object
      Field phoneField = clazz.getDeclaredField(phoneFieldName);
      phoneField.setAccessible(true);
      String phoneNumber = (String) phoneField.get(value);
      if (phoneNumber == null || phoneNumber.isBlank()) {
        // If phone number is blank, no error will be added
        return true;
      }

      // Validate phone number
      String formattedNumber = PhoneNumberUtils.formatToE164(phoneNumber, country);

      if (formattedNumber == null) {
        // Validation failed, add error on the 'phone' field
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Phone number is not in a valid E.164 format")
            .addPropertyNode(phoneFieldName) // Specify the field where the error should appear
            .addConstraintViolation();
        return false; // Return false as the validation failed
      }

      return true; // If formatted correctly, it's valid

    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Handle reflection errors (e.g., if fields don't exist)
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Reflection error occurred while validating")
          .addConstraintViolation();
      return false;
    }
  }
}
