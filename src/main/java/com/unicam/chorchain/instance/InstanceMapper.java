package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Instance;
import com.unicam.chorchain.participantUser.ParticipantUserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring"
//        ,uses = ParticipantUserMapper.class
)
public interface InstanceMapper {
    //    @Mappings({
//            @Mapping(target = "_id", expression = "java( instance._id.toString())"),
//    })
//    @Mappings({
//
//            @Mapping(target = "address", source = "user.address")
//    })
    InstanceDTO toInstanceDTO(Instance instance);
}
