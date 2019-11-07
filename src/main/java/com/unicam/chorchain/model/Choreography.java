package com.unicam.chorchain.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "choreography")
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

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "uploaded_by", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Column(nullable = false)
	private LocalDateTime created;


	@OneToMany(mappedBy = "choreography", cascade = CascadeType.ALL)
	private List<Instance> instances;

	@OneToMany(targetEntity= Participant.class, fetch = FetchType.LAZY)
	private List<Participant> participants;

}
