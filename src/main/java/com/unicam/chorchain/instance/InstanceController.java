package com.unicam.chorchain.instance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@RequestMapping("/instance")
public class InstanceController {

    private final InstanceService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InstanceRequest instanceRequest) {
        log.debug("Create instance {}", instanceRequest);
        service.create(instanceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @GetMapping("{id}")
//    public ResponseEntity<?> listAllInstanceByModel(@PathVariable("id") Long id) {
//        log.debug("Model id - {}", id);
//        return ResponseEntity.status(HttpStatus.OK).body(service.findAllByModelId(id));
//    }

    @RequestMapping(value = "{id}", method = DELETE)
    public String delete(@PathVariable("id") Long id) {
        InstanceDTO instanceDTO = service.read(id);
        log.debug("Model id - {}", id);
        return service.delete(instanceDTO.getId());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> instanceSubscription(@RequestBody InstanceSubscribeRequest instanceSubscribeRequest){
        service.instanceSubscription(instanceSubscribeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/selfSubscribe")
    public ResponseEntity<?> instanceSelfSubscription(@RequestBody InstanceSubscribeRequest instanceSubscribeRequest){
        service.selfSubscription(instanceSubscribeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
