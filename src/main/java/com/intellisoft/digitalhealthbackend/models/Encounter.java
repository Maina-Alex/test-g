package com.intellisoft.digitalhealthbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intellisoft.digitalhealthbackend.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB-ENCOUNTERS")
public class Encounter  extends BaseEntity{
    @NotNull(message = "Patient is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;
    @NotNull(message = "Start date and time is mandatory")
    @PastOrPresent(message = "Start date and time cannot be in the future")
    @Column(nullable = false, name = "encounter-start")
    private LocalDateTime start;
    @PastOrPresent(message = "End date and time cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "encounter-end")
    private LocalDateTime end;
    @NotNull(message = "Encounter date is mandatory")
    @PastOrPresent(message = "Encounter date cannot be in the future")
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date encounterDate;
    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Observation> observations;
}
