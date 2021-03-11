package com.unicam.chorchain.blockchain;

import com.unicam.chorchain.instance.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/blockchain")
@RestController
public class BlockchainController {
    private final BlockchainService service;
    private final InstanceService instanceService;


    @PostMapping("/transaction")
    public  ResponseEntity<?> execute(@RequestBody BlockchainTransaction transaction) throws IOException {
        BlockchainTransaction blockchainTransaction = service.process(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(blockchainTransaction);

    }
}
