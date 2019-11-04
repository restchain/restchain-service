package com.unicam.chorchain.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Document
@Table(name = "model")
@ToString
@EqualsAndHashCode(of="_id")
@NoArgsConstructor
@Getter
@Setter
public class Choreography {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Type(type = "objectid")
	private String _id;

	@Column(nullable = false)
	private String name;

	private String description;

//	private String uploadedBy;
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "uploaded_by", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	@Column(nullable = false)
	private LocalDateTime created;

	@ElementCollection(fetch=FetchType.EAGER)
	private List<String> roles;
	@OneToMany(targetEntity=Instance.class, fetch = FetchType.EAGER)
	private List<Instance> instances;

//	@OneToMany(targetEntity=Instance.class, fetch = FetchType.EAGER)
//	private List<Instance> instances;

}
