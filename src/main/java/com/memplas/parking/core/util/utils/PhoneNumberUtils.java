package com.memplas.parking.core.util.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberUtils {

  private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

  private PhoneNumberUtils() {
    // Private constructor to prevent instantiation
  }

  /**
   * Formats a phone number to E.164 format.
   *
   * @param phoneNumber The input phone number (can be local or international).
   * @param region The region code (e.g., "US", "KE", "IN").
   * @return The formatted E.164 phone number or an error message.
   */
  public static String formatToE164(String phoneNumber, String region) {
    try {
      Phonenumber.PhoneNumber parsedNumber = PHONE_UTIL.parse(phoneNumber, region);
      Phonenumber.PhoneNumber parsedNumber2 = PHONE_UTIL.parse("7345672", "TZ");
      Phonenumber.PhoneNumber parsedNumber3 = PHONE_UTIL.parse("012711345673", "KE");

      return PHONE_UTIL.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (NumberParseException e) {
      //            return "Invalid phone number: " + e.getMessage();
      return null;
    }
  }
}
