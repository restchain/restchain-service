package com.unicam.chorchain.choreography;

import com.unicam.chorchain.model.Choreography;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChoreographyMapper {
    @Mapping(target = "address", source = "uploadedBy.address")
    //Prende dal choreography la proprietà user.adress invece di tutto user
//    @Mapping(target = "participantSize", expression = "java(choreography.getParticipants().size())")
//    @Mapping(target = "instanceSize", expression = "java(choreography.getInstances().size())")
    ChoreographyDTO toDTO(Choreography choreography);
}
