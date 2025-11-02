package com.intellisoft.digitalhealthbackend.service.impl;

import com.intellisoft.digitalhealthbackend.dto.EncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.ObservationWrapper;
import com.intellisoft.digitalhealthbackend.dto.PatientWrapper;
import com.intellisoft.digitalhealthbackend.dto.UniversalResponse;
import com.intellisoft.digitalhealthbackend.enums.Gender;
import com.intellisoft.digitalhealthbackend.exceptions.PatientException;
import com.intellisoft.digitalhealthbackend.models.Encounter;
import com.intellisoft.digitalhealthbackend.models.Observation;
import com.intellisoft.digitalhealthbackend.models.Patient;
import com.intellisoft.digitalhealthbackend.repository.EncounterRepository;
import com.intellisoft.digitalhealthbackend.repository.ObservationRepository;
import com.intellisoft.digitalhealthbackend.repository.PatientRepository;
import com.intellisoft.digitalhealthbackend.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final EncounterRepository encounterRepository;
    private final ObservationRepository observationRepository;

    @Override
    public UniversalResponse createPatient(PatientWrapper patientWrapper) {
        try {
            Optional<Patient> optionalPatient = patientRepository.findByIdentifier(patientWrapper.identifier());
            if (optionalPatient.isPresent()) {
                Patient existingPatient = optionalPatient.get();
                if (existingPatient.getSoftDelete()) {
                    existingPatient.setSoftDelete(false);
                    existingPatient.setBirthDate(formatDate(patientWrapper.birthDate()));
                    existingPatient.setGender(Gender.valueOf(patientWrapper.gender()));
                    existingPatient.setFamilyName(patientWrapper.familyName());
                    existingPatient.setGivenName(patientWrapper.givenName());
                    Patient restoredPatient = patientRepository.save(existingPatient);
                    return UniversalResponse.builder()
                            .status(HttpStatus.OK.value())
                            .message("Patient restored and updated successfully")
                            .data(restoredPatient)
                            .build();
                } else {
                    throw new PatientException("Patient already exists");
                }
            }
            Date birthDate = formatDate(patientWrapper.birthDate());
            Patient patient = Patient.builder()
                    .identifier(patientWrapper.identifier())
                    .birthDate(birthDate)
                    .gender(Gender.valueOf(patientWrapper.gender()))
                    .familyName(patientWrapper.familyName())
                    .givenName(patientWrapper.givenName())
                    .build();
            patientRepository.save(patient);
            return UniversalResponse.builder().status(HttpStatus.OK.value()).message("Patient created successfully").data(patient).build();
        } catch (Exception e) {
            throw new PatientException(e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public UniversalResponse retrievePatient(Long patientId) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId).orElseThrow(() -> new PatientException("Patient not found"));
        return UniversalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Patient retrieved successfully")
                .data(patient).build();
    }

    @Override
    public UniversalResponse updatePatient(Long patientId, PatientWrapper patientWrapper) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId).orElseThrow(() -> new PatientException("Patient not found"));
        if (!(patient.getIdentifier().equals(patientWrapper.identifier()))) {
            patientRepository.findByIdentifierAndSoftDeleteFalse(patientWrapper.identifier())
                    .ifPresent(existingPatient -> {
                        throw new PatientException("Identifier already exists for another patient");
                    });
            patient.setIdentifier(patientWrapper.identifier());
        }
        Date birthDate = formatDate(patientWrapper.birthDate());
        patient.setFamilyName(patientWrapper.familyName());
        patient.setGivenName(patientWrapper.givenName());
        patient.setGender(Gender.valueOf(patientWrapper.gender()));
        patient.setBirthDate(birthDate);
        patientRepository.save(patient);
        return UniversalResponse.builder()
                .data(patient)
                .message("Patient updated successfully")
                .status(HttpStatus.OK.value())
                .build();
    }

    @Override
    public UniversalResponse deletePatient(Long patientId) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId).orElseThrow(() -> new PatientException("Patient not found"));
        if (!patient.getEncounters().isEmpty()) {
            throw new PatientException("Patient has encounters, kindly clear the encounters");
        }
        patient.setSoftDelete(true);
        patientRepository.save(patient);
        return UniversalResponse.builder()
                .message("Patient deleted successfully")
                .status(HttpStatus.OK.value())
                .build();
    }

    @Override
    public UniversalResponse addPatientEncounters(Long patientId, EncounterWrapper encounterWrapper) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId)
                .orElseThrow(() -> new PatientException("Patient not found"));
        List<Encounter> patientEncounters = patient.getEncounters();
        if (patientEncounters == null) {
            patientEncounters = new ArrayList<>();
            patient.setEncounters(patientEncounters);
        }
        Date encounterDate = formatDate(encounterWrapper.encounterDate());
        LocalDateTime startTime = formatDateTime(encounterWrapper.start());
        Encounter encounter = Encounter.builder()
                .encounterDate(encounterDate)
                .start(startTime)
                .patient(patient)
                .build();
        encounterRepository.save(encounter);
        patientEncounters.add(encounter);
        patientRepository.save(patient);
        return UniversalResponse.builder()
                .message("Encounter added successfully")
                .status(HttpStatus.OK.value())
                .data(encounter)
                .build();
    }

    @Override
    public UniversalResponse endPatientEncounter(Long encounterId, String endEncounter) {
        LocalDateTime encounterDateTime = formatDateTime(endEncounter);
        Encounter encounter = encounterRepository.findByIdAndSoftDeleteFalse(encounterId).orElseThrow(() -> new PatientException("Encounter with the given id does not exist"));
        encounter.setEnd(encounterDateTime);
        encounterRepository.save(encounter);
        return UniversalResponse.builder()
                .message("Encounter ended successfully")
                .status(HttpStatus.OK.value())
                .data(encounter)
                .build();
    }

    @Override
    public UniversalResponse retrievePatientEncountersAndObservation(String family, String givenName, Long identifier, String date, int page, int size) {
        try {
            log.info("incoming request  date{}", date);
            Date birthDate = formatDate(date);
            Patient patient = patientRepository.findPatientByFamilyNameAndGivenNameAndIdentifierAndBirthDateAndSoftDeleteFalse(family, givenName, identifier, birthDate).orElseThrow(() -> new PatientException("Patient not found"));
            Pageable pageable = PageRequest.of(page, size);
            Page<Encounter> patientEncounters = encounterRepository.findByPatientIdAndSoftDeleteFalse(patient.getId(), pageable);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("encounters", patientEncounters.getContent());
            responseData.put("currentPage", patientEncounters.getNumber());
            responseData.put("totalPages", patientEncounters.getTotalPages());
            responseData.put("totalEncounters", patientEncounters.getTotalElements());
            responseData.put("hasNext", patientEncounters.hasNext());
            responseData.put("hasPrevious", patientEncounters.hasPrevious());
            return UniversalResponse.builder()
                    .data(responseData)
                    .status(HttpStatus.OK.value())
                    .message("Patient encounters and Observations").build();
        } catch (Exception ex) {
            throw new PatientException(ex.getMessage());
        }

    }

    public UniversalResponse viewPatientEncounters(Long patientId) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId)
                .orElseThrow(() -> new PatientException("Patient not found"));
        List<Encounter> encounters = patient.getEncounters();
        if (encounters == null) {
            encounters = new ArrayList<>();
        }
        return UniversalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Patient encounters retrieved successfully")
                .data(encounters)
                .build();
    }

    public UniversalResponse viewPatientObservations(Long patientId) {
        Patient patient = patientRepository.findByIdAndSoftDeleteFalse(patientId)
                .orElseThrow(() -> new PatientException("Patient not found"));
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC);
        Page<Observation> observationsPage = observationRepository.findObservationByPatientAndSoftDeleteFalse(patient, pageable);
        List<Observation> observations = observationsPage.getContent();
        return UniversalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Patient observations retrieved successfully")
                .data(observations)
                .build();
    }

    @Override
    public UniversalResponse addEncounterObservation(Long encounterId, ObservationWrapper observationWrapper) {
        Encounter encounter = encounterRepository.findByIdAndSoftDeleteFalse(encounterId).orElseThrow(() -> new PatientException("Encounter with the given id does not exist"));
        List<Observation> patientObservations = encounter.getObservations();
        if (patientObservations == null) {
            patientObservations = new ArrayList<>();
            encounter.setObservations(patientObservations);
        }
        LocalDateTime effectiveDateTime = formatDateTime(observationWrapper.effectiveDateTime());
        Observation observation = Observation.builder()
                .code(observationWrapper.code())
                .value(observationWrapper.value())
                .effectiveDateTime(effectiveDateTime)
                .patient(encounter.getPatient())
                .encounter(encounter)
                .build();
        observationRepository.save(observation);
        patientObservations.add(observation);
        encounterRepository.save(encounter);
        return UniversalResponse.builder()
                .message("Encounter added successfully")
                .status(HttpStatus.OK.value())
                .data(encounter)
                .build();
    }

    private Date formatDate(String date) {
        if (date == null || date.isBlank()) {
            throw new PatientException("Date cannot be null or empty");
        }
        try {
            date = date.trim();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new PatientException("Invalid date format. Expected: yyyy-MM-dd (e.g., 1996-08-09), but got: " + date);
        } catch (Exception e) {
            throw new PatientException("Error parsing date: " + e.getMessage());
        }
    }

    private LocalDateTime formatDateTime(String dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTime, formatter);
        } catch (Exception exception) {
            throw new PatientException(exception.getMessage());
        }
    }


}
