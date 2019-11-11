package com.unicam.chorchain.model;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashMap;

@Entity
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class SmartContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @OneToOne
    private Instance instance;

    //TODO capire
    //	@ElementCollection(fetch = FetchType.EAGER)
    //	private List<String> tasks;

    private String abi;
    private String bin;

    //TODO capire
    //	@ElementCollection(fetch = FetchType.EAGER)
    //	private List<String> varNames;

    //@ElementCollection(fetch = FetchType.EAGER)
    //TODO capire
    @Lob
    private LinkedHashMap<String, String> taskIdAndRole = new LinkedHashMap<String, String>();
}
