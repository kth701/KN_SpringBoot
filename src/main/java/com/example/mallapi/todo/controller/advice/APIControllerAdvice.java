package com.example.mallapi.todo.controller.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.log4j.Log4j2;



/* @Controller와  @RestControllerAdvice어노테이션을 이용해서
 *  컨트롤러에서 발생하는 예외를 대신 처리해주는 객체를 생성
 *  AOP(Aspect Oriented Programming): 공통적인 문제의 처리 객체
 */
@RestControllerAdvice
@Log4j2
public class APIControllerAdvice {

    //  유효성 검증 실패 => 예외발생 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e){
        log.error("-> Validation erro: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        // 유효성 검사후 에러발생한 필드에 대한 정보 추출
        e.getBindingResult().getFieldErrors().forEach(error -> {

            String fieldName = error.getField();
            String message = error.getDefaultMessage();

            errors.put(fieldName, message);

        });
        return ResponseEntity.badRequest().body(errors);

    }

    // 잘못된 경로 => 예외처리 발생 => MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentTypeMismatchException e){
        log.error("-> Validation error: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        
        errors.put("error", "Type Mismatched");
        errors.put(e.getName(), e.getValue()+" is not valid value");

        // String fieldName = ex.getName();
        // String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없는 타입";
        // String message = String.format("'%s' 파라미터의 타입이 올바르지 않습니다. '%s' 타입이 필요합니다.", fieldName, requiredType);
        // errors.put("error", "Type Mismatched");
        // errors.put(fieldName, message);


        // return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        
    }

}
