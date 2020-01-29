package com.unicam.chorchain.smartContract;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmartContractShortDTO {
    private String id;
    private String address;
    private String solidity;
//    private String password;
}
