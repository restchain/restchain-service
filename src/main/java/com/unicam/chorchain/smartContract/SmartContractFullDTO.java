package com.unicam.chorchain.smartContract;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmartContractFullDTO {
    private String id;
    private String address;
    private String abi;
    private String bin;
    private LocalDateTime created;
    private String name;
//    private String password;
}
