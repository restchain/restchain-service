package com.unicam.chorchain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "instance")
@ToString
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@Getter
@Setter
public class Instance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
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
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Role.class)
//	private List<Role> freeRoles;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Role.class)
//	private List<Role> freeRolesOptional;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Role.class)
//	private List<Role> mandatoryRoles;
//
//	@Getter
//	@Setter
//	@ElementCollection(fetch = FetchType.EAGER,targetClass = Role.class)
//	private List<Role> optionalRoles;

	private String createdBy;

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
