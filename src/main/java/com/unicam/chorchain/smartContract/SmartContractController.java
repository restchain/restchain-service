package com.unicam.chorchain.smartContract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/contract")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SmartContractController {

    private final SmartContractService service;

    @GetMapping
    public Set<SmartContractDTO> listAllModels(Pageable pageable) {
        return service.getMySmartContracts();
    }

    @GetMapping("{id}")
    public SmartContractFullDTO read(@PathVariable("id") Long id) {
        return service.read(id);
    }
//
//    @ExceptionHandler(SmartContractCompilationException.class)
//    public ResponseEntity<?> smartContractCompilationException(SmartContractCompilationException exc) {
//        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exc.getMessage());
//    }
//
//
//    private final SmartContractService service;

}