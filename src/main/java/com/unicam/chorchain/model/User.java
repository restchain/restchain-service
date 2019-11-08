package com.unicam.chorchain.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToMany(targetEntity = Instance.class, fetch = FetchType.EAGER)
//    private List<Instance> instances;

    @Column(unique = true, nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private Set<ParticipantUser> participantUsers = new HashSet<>();
}