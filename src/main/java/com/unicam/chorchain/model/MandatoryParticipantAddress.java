package com.unicam.chorchain.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mandatory_participants")
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class MandatoryParticipantAddress  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Participant participant;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "created_by", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = true)
    private User user;
}
