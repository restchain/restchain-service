package com.unicam.chorchain.participantUser;

import com.unicam.chorchain.model.ParticipantUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipantUserMapper {
//    @Mapping(target = "userId", source = "user.id")
//    @Mapping(target = "userAddress", source = "user.address")
//    @Mapping(target = "participantId", source = "participant.id")
//    @Mapping(target = "participantName", source = "participant.name")
    ParticipantUserDTO toDTO(ParticipantUser participantUser);
}
