package com.unicam.chorchain.instance;

import com.unicam.chorchain.model.Participant;
import com.unicam.chorchain.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InstanceDTO {
    private Long id;
    private UserDTO user;
    private List<Participant> mandatoryParticipants;
    private LocalDateTime created;
}
