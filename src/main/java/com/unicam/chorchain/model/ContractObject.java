package com.unicam.chorchain.model;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;

@Entity
@Table(name = "contract")
@ToString
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@Getter
@Setter
public class ContractObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private Long id;

	private String address;
	//@ElementCollection(fetch = FetchType.EAGER)
	//private List<String> tasksID;

//	@ElementCollection(fetch = FetchType.EAGER)
//	private List<String> tasks;
	//@ElementCollection(fetch = FetchType.EAGER)
	//private List<String> taskRoles;

	private String abi;

	private String bin;

//	@ElementCollection(fetch = FetchType.EAGER)
//	private List<String> varNames;

	//@ElementCollection(fetch = FetchType.EAGER)
	@Lob
	private LinkedHashMap<String, String> taskIdAndRole = new LinkedHashMap<String, String>();
}
