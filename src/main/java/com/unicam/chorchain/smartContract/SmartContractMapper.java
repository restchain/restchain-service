package com.unicam.chorchain.smartContract;

import com.unicam.chorchain.model.SmartContract;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SmartContractMapper {
    SmartContractDTO toDTO(SmartContract smartContract);

    SmartContractFullDTO toFullDTO(SmartContract smartContract);
}
