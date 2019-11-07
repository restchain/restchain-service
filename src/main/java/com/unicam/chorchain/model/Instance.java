package com.unicam.chorchain.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "instance")
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Choreography choreography;


    //@Pattern(regexp = "^[A-Za-z0-9_-]+$")
    private String name;

    private int actualNumber;

    private int maxNumber;

//	@Getter
//	@Setter
//	@JsonIgnore
//	@ToString.Exclude
//	//TODO capire perchè
//	@OneToMany(targetEntity=User.class, fetch = FetchType.EAGER)
//	@MapKeyColumn(name="role")
//	private Map<String, User> participants = new HashMap<String, User>();

//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Participant.class)
//	private List<Participant> freeRoles;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Participant.class)
//	private List<Participant> freeRolesOptional;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Participant.class)
//	private List<Participant> mandatoryRoles;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Participant.class)
//	private List<Participant> optionalRoles;

    @ManyToMany
    private List<Participant> optionalRoles;

    @ManyToMany
    private List<Participant> mandatoryParticipants;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "created_by", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private boolean done;

//	//@OneToMany(targetEntity=User.class, fetch = FetchType.EAGER)
//	@Getter
//	@Setter
//	@ElementCollection(fetch= FetchType.EAGER)
//	private List<String> visibleAt;

//	@Getter
//	@Setter
//	@OneToOne
//    @JoinColumn( name = "deployedContract_id" )
//	private ContractObject deployedContract;
}
