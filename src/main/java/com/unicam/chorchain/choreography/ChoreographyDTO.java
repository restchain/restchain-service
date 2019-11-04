package com.unicam.chorchain.choreography;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChoreographyDTO {
    private String _id;
    private String name;
    private String description;
    private LocalDateTime created;
    private String address;
}
