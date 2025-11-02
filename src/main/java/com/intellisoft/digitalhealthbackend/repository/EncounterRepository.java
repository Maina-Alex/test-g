package com.intellisoft.digitalhealthbackend.repository;

import com.intellisoft.digitalhealthbackend.models.Encounter;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    Optional<Encounter> findByIdAndSoftDeleteFalse(Long encounterId);

    @Query(
            "SELECT e FROM Encounter e LEFT JOIN FETCH e.observations WHERE e.patient.id ="
                    + " :patientId AND e.softDelete = false")
    Page<Encounter> findByPatientIdAndSoftDeleteFalse(
            @Param("patientId") Long patientId, Pageable pageable);

    @Query(
            "SELECT COUNT(e) FROM Encounter e WHERE e.patient.id = :patientId AND e.softDelete ="
                    + " false")
    long countByPatientIdAndSoftDeleteFalse(@Param("patientId") Long patientId);
}
