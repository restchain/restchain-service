package com.unicam.chorchain.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@Table(name = "instance")
@ToString
@EqualsAndHashCode(of = "_id")
@NoArgsConstructor
@Getter
@Setter
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    private String _id;

    //@Pattern(regexp = "^[A-Za-z0-9_-]+$")
    private String name;
    private int actualNumber;
    private int maxNumber;
    @OneToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    @MapKeyColumn(name = "role")
    private Map<String, User> participants = new HashMap<String, User>();
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> freeRoles;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> freeRolesOptional;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> mandatoryRoles;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> optionalRoles;
    private String createdBy;
    private boolean done;
    //@OneToMany(targetEntity=User.class, fetch = FetchType.EAGER)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> visibleAt;
    @OneToOne
    @JoinColumn(name = "deployedContract_id")
    private ContractObject deployedContract;
}
