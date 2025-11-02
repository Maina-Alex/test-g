package com.intellisoft.digitalhealthbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_encounter")
@AttributeOverride(name = "softDelete", column = @Column(name = "soft-delete", nullable = false))
public class Encounter extends BaseEntity {

    @Column(nullable = false, name = "encounter-start")
    private LocalDateTime start;

    @Column(name = "encounter-end")
    private LocalDateTime end;

    @Column(name = "encounter_date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date encounterDate;

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Observation> observations;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;
}
