package com.unicam.chorchain.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String address;
    private LocalDateTime created;
//    private String password;
}
