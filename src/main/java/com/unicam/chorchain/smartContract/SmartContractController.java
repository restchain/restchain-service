package com.unicam.chorchain.smartContract;

import com.unicam.chorchain.instance.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/smart_contract")
public class SmartContractController {

//    private final InstanceService instanceService;
//
//    @PostMapping("/deploy")
//    public ResponseEntity<?> instanceDeploy(@RequestBody Long id) throws SmartContractDeployException, IOException {
//        service.create(instanceService.findInstanceById(id));
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @ExceptionHandler(SmartContractCompilationException.class)
//    public ResponseEntity<?> smartContractCompilationException(SmartContractCompilationException exc) {
//        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exc.getMessage());
//    }
//
//
//    private final SmartContractService service;

}