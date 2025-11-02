package com.intellisoft.digitalhealthbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Observation extends BaseEntity {
    @NotNull(message = "Patient is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;
    @NotNull(message = "Encounter is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false, foreignKey = @ForeignKey(name = "fk_observation_encounter"))
    @JsonBackReference
    private Encounter encounter;
    @NotBlank(message = "Observation code is mandatory")
    @Size(min = 1, max = 50, message = "Code must be between 1 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Code must contain only uppercase letters, numbers, underscores, and hyphens")
    @Column(nullable = false, length = 50)
    private String code;
    @NotBlank(message = "Observation value is mandatory")
    @Size(max = 500, message = "Value cannot exceed 500 characters")
    @Column(name = "observation-value", nullable = false, length = 500)
    private String value;
    @NotNull(message = "Effective date and time is mandatory")
    @PastOrPresent(message = "Effective date and time cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    private LocalDateTime effectiveDateTime;

    private boolean isValidEffectiveTime() {
        if (effectiveDateTime == null || encounter == null || encounter.getStart() == null) return false;
        if (effectiveDateTime.isBefore(encounter.getStart())) return false;
        return encounter.getEnd() == null || !effectiveDateTime.isAfter(encounter.getEnd());
    }
}
