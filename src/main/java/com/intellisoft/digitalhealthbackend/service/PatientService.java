package com.intellisoft.digitalhealthbackend.service;

import com.intellisoft.digitalhealthbackend.dto.EncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.ObservationWrapper;
import com.intellisoft.digitalhealthbackend.dto.PatientWrapper;
import com.intellisoft.digitalhealthbackend.dto.UniversalResponse;

public interface PatientService {
    UniversalResponse createPatient(PatientWrapper patientWrapper);

    UniversalResponse retrievePatient(Long patientId);

    UniversalResponse updatePatient(Long patientId, PatientWrapper patientWrapper);

    UniversalResponse deletePatient(Long patientId);

    UniversalResponse addPatientEncounters(Long patientId, EncounterWrapper encounterWrapper);

    UniversalResponse addEncounterObservation(Long encounterId, ObservationWrapper observationWrapper);

    UniversalResponse endPatientEncounter(Long encounterId, String endEncounter);

    UniversalResponse retrievePatientEncountersAndObservation(String family, String givenName, Long identifier, String date, int page, int size);

    UniversalResponse viewPatientEncounters(Long patientId);

    UniversalResponse viewPatientObservations(Long patientId);
}
