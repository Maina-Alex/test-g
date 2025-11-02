package com.intellisoft.digitalhealthbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record PatientWrapper(
        @NotNull(message = "Identifier is mandatory")
        @Min(value = 1000000, message = "ID number must be at least 7 digits")
        @Max(value = 99999999, message = "ID number must be at most 8 digits")        @Pattern(regexp = "^\\d{7,8}$", message = "ID number must be 7-8 digits")
        Long identifier,
        @NotBlank(message = "GivenName is mandatory")
        @Size(min = 1, max = 100, message = "Given name must be between 1 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Given name contains invalid characters")
        String givenName,
        @NotBlank(message = "FamilyName is mandatory")
        @Size(min = 1, max = 100, message = "Family name must be between 1 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Family name contains invalid characters")
        String familyName,
        @NotNull(message = "Gender is mandatory")
        String gender,
        @NotNull(message = "birthDate is mandatory")
        String birthDate
) {
}
