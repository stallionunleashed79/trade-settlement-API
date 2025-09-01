package com.tradesettlement.trade_service.controller.advice;
 
import com.tradesettlement.trade_service.exceptions.DuplicateFileException;
import com.tradesettlement.trade_service.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class TradeControllerAdvice {

  @ExceptionHandler(DuplicateFileException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateFileException(final DuplicateFileException ex) {
    final ErrorResponse errorResponse = ErrorResponse.builder()
            .error(ex.getMessage())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
  }
}