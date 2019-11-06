package com.unicam.chorchain.instance;

import com.unicam.chorchain.common.CommonMapper;
import com.unicam.chorchain.model.Instance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface InstanceMapper {
//    @Mappings({
//            @Mapping(target = "_id", expression = "java( instance._id.toString())"),
//    })
    InstanceDTO toInstanceDTO(Instance instance);
}
