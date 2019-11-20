package com.unicam.chorchain.smartContract;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmartContractDTO {
    private String id;
    private String address;
    private LocalDateTime created;
//    private String password;
}
