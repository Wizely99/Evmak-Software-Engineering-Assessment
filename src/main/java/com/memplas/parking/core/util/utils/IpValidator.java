package com.memplas.parking.core.util.utils;

public class IpValidator {

  /** Validates if the given string is a valid IPv4 address. */
  public static boolean isValidIpv4(String ip) {
    if (ip == null || ip.isEmpty()) {
      return false;
    }

    String[] octets = ip.split("\\.");
    if (octets.length != 4) {
      return false;
    }

    try {
      for (String octet : octets) {
        int value = Integer.parseInt(octet);
        if (value < 0 || value > 255) {
          return false;
        }
        // Check for invalid leading zeros (e.g., "01.02.03.04")
        if (octet.length() > 1 && octet.startsWith("0")) {
          return false;
        }
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }

  /** Validates if the given string is a valid IPv6 address. */
  public static boolean isValidIpv6(String ip) {
    if (ip == null || ip.isEmpty()) {
      return false;
    }

    // Remove IPv6 zone index if present
    int zoneIndex = ip.indexOf('%');
    if (zoneIndex != -1) {
      ip = ip.substring(0, zoneIndex);
    }

    String[] groups = ip.split(":");
    if (groups.length < 3 || groups.length > 8) {
      return false;
    }

    // Check for empty groups (::)
    int emptyGroupCount = 0;
    for (String group : groups) {
      if (group.isEmpty()) {
        emptyGroupCount++;
        if (emptyGroupCount > 1) {
          return false;
        }
      } else {
        // Validate each hexadecimal group
        if (group.length() > 4) {
          return false;
        }
        try {
          int value = Integer.parseInt(group, 16);
          if (value < 0 || value > 65535) {
            return false;
          }
        } catch (NumberFormatException e) {
          return false;
        }
      }
    }

    return true;
  }

  /** Validates if the given string is either a valid IPv4 or IPv6 address. */
  public static boolean isValidIp(String ip) {
    return isValidIpv4(ip) || isValidIpv6(ip);
  }
}
