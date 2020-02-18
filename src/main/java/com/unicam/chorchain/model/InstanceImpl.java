package com.unicam.chorchain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class InstanceImpl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime created;

    @ManyToOne(optional = false)
    private User createdBy;

    private String name;

    @Lob
    private String file;

    public InstanceImpl(LocalDateTime created, User createdBy, String name, String file) {
        this.created = created;
        this.createdBy = createdBy;
        this.name = name;
        this.file = file;
    }
}