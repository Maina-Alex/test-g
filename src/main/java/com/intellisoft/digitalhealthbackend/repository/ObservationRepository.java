package com.intellisoft.digitalhealthbackend.repository;

import com.intellisoft.digitalhealthbackend.models.Observation;
import com.intellisoft.digitalhealthbackend.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    Page<Observation> findObservationByPatientAndSoftDeleteFalse(Patient patientId, Pageable pageable);
}
