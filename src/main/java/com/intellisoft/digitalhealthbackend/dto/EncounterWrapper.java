package com.intellisoft.digitalhealthbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
@Builder
public record EncounterWrapper(
        @NotNull(message = "Patient is mandatory")
        Long patient,
        @NotNull(message = "Start date and time is mandatory")
        String start,
        @NotNull(message = "Encounter date is mandatory")
        String encounterDate
) {
}
