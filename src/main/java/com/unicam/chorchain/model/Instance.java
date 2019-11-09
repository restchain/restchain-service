package com.unicam.chorchain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Choreography choreography;

    @CreatedDate
    private LocalDateTime created;

    //@Pattern(regexp = "^[A-Za-z0-9_-]+$")
//    private String name;

//    private int actualNumber;

//    private int maxNumber;

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

    @OneToMany(mappedBy = "instance")
    private Set<InstanceParticipantUser> mandatoryParticipants = new HashSet<>(0);

    @ManyToOne(optional = false)
    private User createdBy;

    public Instance(Choreography choreography, LocalDateTime created, User createdBy) {
        this.choreography = choreography;
        this.created = created;
        this.createdBy = createdBy;
    }


    @Transient
    public boolean isDone() {
        return getMandatoryParticipants().stream()
                .allMatch((m) -> m.getUser() != null);
    }

    @Transient
    public int getPending() {
        return (int) getMandatoryParticipants().stream()
                .filter((m) -> m.getUser() == null).count();
    }
};


//    private boolean done;

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
