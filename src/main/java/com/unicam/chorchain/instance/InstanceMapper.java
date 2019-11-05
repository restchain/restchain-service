package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Instance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstanceMapper {
    InstanceDTO toInstanceDTO(Instance instance);
}
