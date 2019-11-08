package com.unicam.chorchain.user;

import com.unicam.chorchain.PagedResources;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/users/id/{id}")
    public UserDTO readById(@PathVariable("id") String _id) {
        return service.read(_id);
    }

    @GetMapping("/users/{address}")
    public UserDTO readByAddress(@PathVariable("address") String address) {
        return service.readByAddress(address);
    }

    @GetMapping("/users")
    public PagedResources<UserDTO> listAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    //New user
    @PostMapping(value = "/signin")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserRequest userReq) {
        return service.create(userReq);
    }
}
