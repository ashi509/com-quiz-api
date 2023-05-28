package com.quiz.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;

@RestControllerAdvice
@Slf4j
public class GlobalException {
  @ExceptionHandler(CustomException.class)
  @ResponseBody
  public ResponseEntity<?> handleCustomException(CustomException customException){
      log.error(customException.getErrorMessage());
      return ResponseEntity.status(customException.getStatusCode()).body(customException.getErrorMessage());
  }

    @ExceptionHandler(ParseException.class)
    @ResponseBody
    public ResponseEntity<?> handleParseException(ParseException parseException){
        log.error(parseException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(parseException.getMessage());
    }

}
