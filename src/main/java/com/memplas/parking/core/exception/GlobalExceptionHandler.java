package com.memplas.parking.core.exception;

import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// @RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {

        logger.error("Exception occurred at path: {}", getPath(request), ex);
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        logger.error("Exception occurred at path: {}", getPath(request), ex);
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Validation error", errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 1. Invalid Request Data (400)
    //    @ExceptionHandler(HttpMessageNotReadableException.class)
    //    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException
    // ex, WebRequest request) {
    //        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    //        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    //    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        // Extracting the root cause of the exception
        String errorMessage = "Malformed JSON request";

        // Check if the cause is a JsonParseException (which would be related to invalid JSON)
        if (ex.getCause() instanceof JsonParseException) {
            JsonParseException jsonParseException = (JsonParseException) ex.getCause();
            errorMessage = "Invalid JSON syntax: " + jsonParseException.getMessage();
        }

        // Create an ApiError response with a generic or detailed message
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, null);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 2. Unique Constraint Violations (409)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        String message = "Database error";
        //
        //        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
        //            message = "Data already exists";
        //        }
        Throwable rootCause = ex.getRootCause(); // Get the deepest cause of the error

        if (rootCause instanceof ConstraintViolationException constraintEx) {
            message =
                    "Constraint parkingviolation: "
                            + constraintEx.getConstraintViolations()
                            + "---"
                            + constraintEx.getMessage();
        } else if (rootCause != null) {
            message =
                    rootCause.getMessage()
                            + "end"
                            + rootCause.getCause()
                            + "---"
                            + rootCause.getMessage().toUpperCase(); // Extract detailed error message
        }
        ApiError error = new ApiError(HttpStatus.CONFLICT, message);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 3. Method Not Allowed (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 4. Missing Path Variables (400)
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiError> handleMissingPathVariable(
            MissingPathVariableException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 5. Missing Request Parameters (400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParams(
            MissingServletRequestParameterException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 6. Type Mismatch Exceptions (400)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(TypeMismatchException ex, WebRequest request) {
        String message =
                String.format(
                        "Parameter '%s' should be of type %s",
                        ex.getPropertyName(), ex.getRequiredType().getSimpleName());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 7. Bean Validation Errors (400)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        // Map each parkingviolation to a key-value pair of field and error message
        Map<String, String> errors =
                ex.getConstraintViolations().stream()
                        .collect(
                                Collectors.toMap(
                                        violation -> violation.getPropertyPath().toString(), // field name
                                        ConstraintViolation::getMessage // validation message
                                ));

        // Create the ApiError response
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Validation error", errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 8. Access Denied (403)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN, "Access denied");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 9. SQL Exceptions (500)
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiError> handleSQLException(SQLException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 10. File Upload Exceptions (400)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxSizeException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "File size exceeds maximum limit");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Helper method to get request path
    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    /**
     * Handles TransactionSystemException and its root causes.
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionSystemException(
            TransactionSystemException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "A transaction error occurred.");
        response.put("status", HttpStatus.BAD_REQUEST.value());

        // Extract the root cause
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof RollbackException) {
            RollbackException rollbackEx = (RollbackException) rootCause;
            if (rollbackEx.getCause() instanceof ConstraintViolationException) {
                // Handle constraint violations
                ConstraintViolationException constraintEx =
                        (ConstraintViolationException) rollbackEx.getCause();
                Set<ConstraintViolation<?>> violations = constraintEx.getConstraintViolations();
                response.put(
                        "errors",
                        violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
            } else {
                response.put("errors", rollbackEx.getMessage());
            }
        } else {
            response.put("errors", rootCause != null ? rootCause.getMessage() : ex.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //    /**
    //     * Handles other unexpected exceptions.
    //     */
    //    @ExceptionHandler(Exception.class)
    //    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest
    // request) {
    //        Map<String, Object> response = new HashMap<>();
    //        response.put("message", "An unexpected error occurred.");
    //        response.put("errors", ex.getMessage());
    //        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    //
    //        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    //    }
}
