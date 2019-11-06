package com.unicam.chorchain.choreography;

import com.unicam.chorchain.model.Choreography;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChoreographyMapper {
    @Mapping(target = "address",source = "user.address") //Prende dal CHoreogrphy la proprietà user.adress invece di tutto user
    @Mapping(target = "participants",source = "participants") //Prende dal CHoreogrphy la proprietà user.adress
    ChoreographyDTO toDTO(Choreography choreography);
}
