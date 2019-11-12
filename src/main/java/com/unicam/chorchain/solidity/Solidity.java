package com.unicam.chorchain.solidity;

import com.unicam.chorchain.smartContract.SmartContractService;
import lombok.Builder;

import javax.validation.constraints.NotEmpty;

@Builder
public class Solidity {
    @NotEmpty
    private String pragmaVersion;
    @NotEmpty
    private String fileName;

    public String toString(){
        StringBuffer out = new StringBuffer();
        out.append("pragma solidity ").append(pragmaVersion).append(";\n");
        out.append("pragma experimental ABIEncoderV2;\n") ;
        out.append("\n");
        out.append("contract ").append(fileName).append("{\n");
        out.append("\tuint counter;\n");
        out.append("\tevent stateChanged(uint);\n");
        out.append("\tmapping(string => uint) position;\n");
        out.append("\tenum State {DISABLED, ENABLED, DONE} State s;\n");
        out.append("\tmapping(string => string) operator;\n");
        out.append("}");
        return out.toString();
    }}
/*    pragma solidity ^0.5.3;
    pragma experimental ABIEncoderV2;

    contract ShopNew {
        uint counter;
        event stateChanged(uint);
        mapping(string => uint) position;
        enum State {DISABLED, ENABLED, DONE} State s;
        mapping(string => string) operator;

        */
