package com.unicam.chorchain.choreography;

import com.unicam.chorchain.common.CommonMapper;
import com.unicam.chorchain.instance.InstanceMapper;
import com.unicam.chorchain.model.Choreography;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = InstanceMapper.class)
public interface ChoreographyMapper {
    @Mapping(target = "address",source = "user.address") //Prende dal CHoreogrphy la proprietà user.adress invece di tutto user
    ChoreographyDTO toDTO(Choreography choreography);
}
