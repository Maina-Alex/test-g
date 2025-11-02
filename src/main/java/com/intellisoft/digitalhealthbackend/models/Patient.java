package com.intellisoft.digitalhealthbackend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intellisoft.digitalhealthbackend.enums.Gender;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_patient")
public class Patient extends BaseEntity {

    @Column(name = "identifier", unique = true, nullable = false)
    private Long identifier;

    @Column(name = "given_name", nullable = false, length = 100)
    private String givenName;

    @Column(name = "family_name", nullable = false, length = 100)
    private String familyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @JsonManagedReference
    private List<Encounter> encounters = new ArrayList<>();
}
