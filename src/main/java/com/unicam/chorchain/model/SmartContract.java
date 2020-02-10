package com.unicam.chorchain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

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

    @OneToOne(mappedBy = "smartContract")
    private Instance instance;

    @CreatedDate
    private LocalDateTime created;

    //TODO capire - tutti i nome gli oggetti BPMN proccessati - BPMNObjectNames
    @ElementCollection
    private Set<String> functionSignatures = new HashSet<>();

    @Lob
    private String abi;  //il contenuto abi
    @Lob
    private String bin;  //il contenuto bin

    @Lob
    private String solidity;

    //TODO capire  - Prende i nomi delle variabili globali presenti in  struct StateMemory {
    //	@ElementCollection(fetch = FetchType.EAGER)
    //	private List<String> varNames;

    //@ElementCollection(fetch = FetchType.EAGER)
    //TODO capire
    //Qui ci server per capire poi i permessi degli oggetti poi nel FE - BPMNObjectIdParticipant
    @Lob
    private LinkedHashMap<String, String> taskIdAndRole = new LinkedHashMap<String, String>();

    public SmartContract(String contractAddress, String abi, String bin, Instance instance, Set<String> functionSignatures, String solidity) {
        this.abi = abi;
        this.bin = bin;
        this.address = contractAddress;
        this.instance = instance;
        this.created = LocalDateTime.now();
        this.functionSignatures.addAll(functionSignatures);
        this.solidity = solidity;
    }
}
