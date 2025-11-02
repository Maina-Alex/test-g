package com.intellisoft.digitalhealthbackend.repository;

import com.intellisoft.digitalhealthbackend.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByIdentifierAndSoftDeleteFalse(Long identifier);

    Optional<Patient> findByIdAndSoftDeleteFalse(Long id);

    Optional<Patient> findPatientByFamilyNameAndGivenNameAndIdentifierAndBirthDateAndSoftDeleteFalse(String family, String givenName, Long identifier, Date birthDate);

    Optional<Patient> findByIdentifier(Long identifier);
}
