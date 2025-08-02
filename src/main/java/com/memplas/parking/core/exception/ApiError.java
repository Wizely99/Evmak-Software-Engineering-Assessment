package com.memplas.parking.core.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public record ApiError(HttpStatus status, String message, Map<String, String> errors) {
  public ApiError(HttpStatus status, String message) {
    this(status, message, null);
  }

  public ApiError(HttpStatus status, String message, Map<String, String> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }
}
