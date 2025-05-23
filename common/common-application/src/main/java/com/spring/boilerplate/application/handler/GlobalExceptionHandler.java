package com.spring.boilerplate.application.handler;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = {Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleException(Exception exception) {
    return ErrorDTO.builder()
        .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message(exception.getMessage())
        .build();
  }

  @ResponseBody
  @ExceptionHandler(value = {ValidationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleException(ValidationException validationException) {
    ErrorDTO errorDTO;
    if (validationException instanceof ConstraintViolationException) {
      errorDTO =
          ErrorDTO.builder()
              .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
              .message(
                  extractViolationsFromException(
                      (ConstraintViolationException) validationException))
              .build();
    } else {
      errorDTO =
          ErrorDTO.builder()
              .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
              .message(validationException.getMessage())
              .build();
    }
    return errorDTO;
  }

  private String extractViolationsFromException(
      ConstraintViolationException constraintViolationException) {
    return constraintViolationException.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("--"));
  }
}
