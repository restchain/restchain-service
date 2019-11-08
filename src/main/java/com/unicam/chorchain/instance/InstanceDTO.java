package com.unicam.chorchain.instance;

import com.unicam.chorchain.participantUser.ParticipantUserDTO;
import com.unicam.chorchain.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InstanceDTO {
    private Long id;
    private UserDTO user;
    private List<ParticipantUserDTO> mandatoryParticipants;
    private LocalDateTime created;
}
