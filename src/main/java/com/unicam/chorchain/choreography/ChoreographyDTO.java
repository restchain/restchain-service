package com.unicam.chorchain.choreography;

import com.unicam.chorchain.instance.InstanceDTO;
import com.unicam.chorchain.model.Participant;
import com.unicam.chorchain.participant.ParticipantDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChoreographyDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime created;
    private String  address;
    private List<ParticipantDTO> participants;
//    private int participantSize;
    private List<InstanceDTO> instances;
//    private int instanceSize;
}
