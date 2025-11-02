package com.intellisoft.digitalhealthbackend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intellisoft.digitalhealthbackend.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB-PATIENTS", indexes = @Index(columnList = "identifier", unique = true))
public class Patient extends BaseEntity {
    @NotNull(message = "Identifier is mandatory")
    @Min(value = 1000000, message = "ID number must be at least 7 digits")
    @Max(value = 99999999, message = "ID number must be at most 8 digits")
    @Column(name = "identifier", unique = true, nullable = false)
    private Long identifier;
    @NotBlank(message = "GivenName is mandatory")
    @Size(min = 1, max = 100, message = "Given name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Given name contains invalid characters")
    private String givenName;
    @NotBlank(message = "FamilyName is mandatory")
    @Size(min = 1, max = 100, message = "Family name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Family name contains invalid characters")
    private String familyName;
    @NotNull(message = "Gender is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @JsonManagedReference
    private List<Encounter> encounters = new ArrayList<>();
}
