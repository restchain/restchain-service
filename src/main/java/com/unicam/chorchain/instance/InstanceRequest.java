package com.unicam.chorchain.instance;

import lombok.Data;

import java.util.List;

@Data
public class InstanceRequest {
    List<String> visibleAt;
    List<String> mandatoryRoles;
    List<String> optionalRoles;
}
