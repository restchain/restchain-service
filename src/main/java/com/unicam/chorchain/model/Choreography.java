package com.unicam.chorchain.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@Getter
@Setter
public class Choreography {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String description;

	@ManyToOne(optional = false)
	private User uploadedBy;

	@Column(nullable = false)
	private LocalDateTime created;


	@OneToMany(mappedBy = "choreography") //Direttamente agganciata
	//TODO - aggiungi set(0)
	private Set<Instance> instances = new HashSet<>(0);

	@OneToMany(mappedBy = "choreography")
	private Set<Participant> participants = new HashSet<>(0);

}
