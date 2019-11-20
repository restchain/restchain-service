package com.unicam.chorchain.error;

import com.unicam.chorchain.smartContract.SmartContractAlreadyExistsException;
import com.unicam.chorchain.smartContract.SmartContractCompilationException;
import com.unicam.chorchain.smartContract.SmartContractDeployException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityExistsException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity accreditationServiceBadRequestExceptionHandler(EntityExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(SmartContractCompilationException.class)
    public ResponseEntity<?> smartContractCompilationException(SmartContractCompilationException exc) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exc.getMessage());
    }

    @ExceptionHandler(SmartContractDeployException.class)
    public ResponseEntity<?> smartContractDeployException(SmartContractCompilationException exc) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exc.getMessage());
    }

    @ExceptionHandler(SmartContractAlreadyExistsException.class)
    public ResponseEntity<?> smartContractAlreadyExistsException(SmartContractCompilationException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exc.getMessage());
    }
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity entityNotFoundExceptionHandler(EntityNotFoundException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createMessage(e.getMessage()));
//    }
//
//
//    public Map<String, Object> createError(HttpStatus status, Object errors) {
//        return ImmutableMap.<String, Object>builder()
//                .put("message", status.getReasonPhrase())
////                .put("status", status.value())
//                .put("errors", errors)
//                .build();
//    }
//
//
//    public static Map<String, String> createMessage(String message) {
//        if ( Strings.isNullOrEmpty(message) ) {
//            return null;
//        }
//
//        return ImmutableMap.of("message", message);
//    }
}
