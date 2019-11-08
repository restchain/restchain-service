package com.unicam.chorchain.participantUser;

import com.unicam.chorchain.model.Participant;
import com.unicam.chorchain.participant.ParticipantDTO;
import com.unicam.chorchain.user.UserDTO;
import lombok.Data;

@Data
public class ParticipantUserDTO {
    private Long id;
//    private Long participantId;
//    private String participantName;
//    private Long userId;
//    private String userAddress;
    private ParticipantDTO participant;
    private UserDTO user;
}
