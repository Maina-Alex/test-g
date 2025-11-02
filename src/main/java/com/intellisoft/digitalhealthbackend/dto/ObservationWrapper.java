package com.intellisoft.digitalhealthbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Builder
public record ObservationWrapper(
        @NotBlank(message = "Observation code is mandatory")
        @Size(min = 1, max = 50, message = "Code must be between 1 and 50 characters")
        @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Code must contain only uppercase letters, numbers, underscores, and hyphens")
        String code,
        @NotBlank(message = "Observation value is mandatory")
        @Size(max = 500, message = "Value cannot exceed 500 characters")
        String value,
        @NotNull(message = "Effective date and time is mandatory")
        String effectiveDateTime
) {
}
