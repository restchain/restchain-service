package com.unicam.chorchain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "users")
@ToString
@EqualsAndHashCode(of = "_id")
@NoArgsConstructor
@Getter
@Setter
@Document
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public ObjectId _id;

//    @OneToMany(targetEntity = Instance.class, fetch = FetchType.EAGER)
//    private List<Instance> instances;


    @Column(unique = true, nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

}