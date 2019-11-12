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

    //TODO capire - tutti i nome gli oggetti BPMN proccessati - BPMNObjectNames
    //	@ElementCollection(fetch = FetchType.EAGER)
    //	private List<String> tasks;

    private String abi;  //il contenuto abi
    private String bin;  //il contenuto bin

    //TODO capire  - Prende i nomi delle variabili globali presenti in  struct StateMemory {
    //	@ElementCollection(fetch = FetchType.EAGER)
    //	private List<String> varNames;

    //@ElementCollection(fetch = FetchType.EAGER)
    //TODO capire
    //Qui ci server per capire poi i permessi degli oggetti poi nel FE - BPMNObjectIdParticipant
    @Lob
    private LinkedHashMap<String, String> taskIdAndRole = new LinkedHashMap<String, String>();
}
