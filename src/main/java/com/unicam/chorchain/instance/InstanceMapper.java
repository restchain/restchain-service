package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Instance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InstanceMapper {
//    @Mappings({
//            @Mapping(target = "_id", expression = "java( instance._id.toString())"),
//    })
    InstanceDTO toInstanceDTO(Instance instance);
}
