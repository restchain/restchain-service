package com.unicam.chorchain.smartContract;

import com.unicam.chorchain.model.SmartContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SmartContractMapper {
    @Mapping(target = "name", source = "instance.choreography.name")
    SmartContractDTO toDTO(SmartContract smartContract);
    @Mapping(target = "name", source = "instance.choreography.name")
    SmartContractFullDTO toFullDTO(SmartContract smartContract);
}
