package com.unicam.chorchain.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "role")
@ToString
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Participant(String name){
        this.setName(name);
    }
}


