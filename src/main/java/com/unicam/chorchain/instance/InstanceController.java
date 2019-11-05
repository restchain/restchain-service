package com.unicam.chorchain.instance;

import com.unicam.chorchain.PagedResources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@RequestMapping("/instance")
public class InstanceController {

    private final InstanceService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InstanceRequest instanceRequest) {
        log.debug("{}", instanceRequest);
        service.create(instanceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
