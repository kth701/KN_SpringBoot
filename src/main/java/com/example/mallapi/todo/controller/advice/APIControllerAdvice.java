package com.example.mallapi.todo.controller.advice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.example.mallapi.util.CustomJWTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
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


    // EntityNotFoundException: RuntimeException클래스 상속 받은 직접 제작한 예외처리
    // 서비스 계층에서 ID로 엔티티를 찾을 수 없을 때 발행 : EntityNotFoundException
    // 예외 발생이 안됨. => NoSuchElementException예외발생으로 처리됨.
   @ExceptionHandler(EntityNotFoundException.class)
   public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e){
       log.error("-> EntityNotFoundException: {}", e.getMessage());

       Map<String, String> errors = new HashMap<>();
       // 예외 객체에 포함된 상세 메시지를 반환하여 클라이언트가 원인을 명확히 알 수 있도록 합니다.       
       errors.put("error", e.getMessage());  // key는 "error" 설정시 메시지 정상
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
   }
    /*
    // JPA findById().get() 등에서 발생할 수 있는 예외 처리=> EntityNotFoundException대신 사용
    // TodoServiceImp에서 result.orElseThrow()=> 예외처리를 사용정의 클래스 사용하지 않으면 작동됨
    // @ExceptionHandler(NoSuchElementException.class)
    // public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
    //     log.error("-> NoSuchElementException: {}", e.getMessage());

    //     Map<String, String> errors = new HashMap<>();
    //     //errors.put("error", "Requested resource was not found.");
    //     errors.put("error", "Entity Not Found");

    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    // }
     */



    /**
     * JWT 관련 예외(`CustomJWTException`)가 발생했을 때 처리하는 핸들러
     * 토큰이 만료되었거나, 형식이 잘못되었거나, 서명이 유효하지 않은 경우 등에 호출
     *
     * @param e 발생한 `CustomJWTException` 객체
     * @return HTTP 401 Unauthorized 상태 코드와 오류 메시지를 담은 `ResponseEntity`
     */
    @ExceptionHandler(CustomJWTException.class)
    public ResponseEntity<?> handleJWTException(CustomJWTException e) {
        log.error("CustomJWTException: {}", e.getMessage());

        // HTTP 상태 코드 401 (Unauthorized)와 함께 오류 메시지를 담은 Map을 응답 본문으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("error", e.getMessage()));
    }

}
