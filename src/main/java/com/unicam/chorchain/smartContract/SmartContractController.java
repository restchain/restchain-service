package com.unicam.chorchain.smartContract;

import com.unicam.chorchain.PagedResources;
import com.unicam.chorchain.choreography.ChoreographyDTO;
import com.unicam.chorchain.model.InstanceParticipantUser;
import com.unicam.chorchain.model.SmartContract;
import com.unicam.chorchain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RequestMapping("/contract")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SmartContractController {


    //    private final InstanceService instanceService;
    private final SmartContractService service;


    @GetMapping
    public Set<SmartContractFullDTO> listAllModels(Pageable pageable) {
        return service.getMySmartContract();
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