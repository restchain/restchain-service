package com.unicam.chorchain.instance;

import com.unicam.chorchain.instanceParticipantUser.InstanceParticipantUserMapper;
import com.unicam.chorchain.model.Instance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = InstanceParticipantUserMapper.class)
public interface InstanceMapper {
//    @Mappings({
////            @Mapping(target = "done", expression = "java( instance.getMandatoryParticipants().stream().filter((m)-> m.getUser()!=null).collect(java.util.stream.Collectors.toList()).size()==instance.getMandatoryParticipants().size())"),
//    })

//    @Mappings({
//
//            @Mapping(target = "address", source = "user.address")
//    })

    InstanceDTO toInstanceDTO(Instance instance);
}
