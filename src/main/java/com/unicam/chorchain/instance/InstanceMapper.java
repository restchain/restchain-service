package com.unicam.chorchain.instance;

import com.unicam.chorchain.instanceParticipantUser.InstanceParticipantUserMapper;
import com.unicam.chorchain.model.Instance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = InstanceParticipantUserMapper.class)
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
