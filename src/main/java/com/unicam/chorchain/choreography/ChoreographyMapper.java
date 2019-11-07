package com.unicam.chorchain.choreography;

import com.unicam.chorchain.model.Choreography;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChoreographyMapper {
    @Mapping(target = "address", source = "user.address")
    //Prende dal choreography la proprietà user.adress invece di tutto user
    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "instances", source = "instances")
    ChoreographyDTO toDTO(Choreography choreography);
}
