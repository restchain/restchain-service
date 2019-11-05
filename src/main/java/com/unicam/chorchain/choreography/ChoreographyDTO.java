package com.unicam.chorchain.choreography;

import com.unicam.chorchain.model.Instance;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChoreographyDTO {
    private String _id;
    private String name;
    private String description;
    private LocalDateTime created;
    private String address;
    private List<String> roles;
    private List<Instance> instances;
}
