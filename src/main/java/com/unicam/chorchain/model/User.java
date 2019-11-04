package com.unicam.chorchain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToMany(targetEntity = Instance.class, fetch = FetchType.EAGER)
//    private List<Instance> instances;


    @Column(unique = true, nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

}