package com.intellisoft.digitalhealthbackend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@Nested
@ExtendWith(MockitoExtension.class)
@Slf4j
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;

    @Mock private ObservationRepository observationRepository;

    @Mock private EncounterRepository encounterRepository;

    @InjectMocks private PatientServiceImpl patientServiceImpl;
    private Long patientId;
    private Patient testPatient;
    private Encounter encounter;
    private Observation testObservation;
    private ObservationWrapper testObservationWrapper;
    String family;
    String givenName;
    Long identifier;
    String birthDate;

    @BeforeEach
    void setUp() {
        patientId = 1L;
        testPatient =
                Patient.builder()
                        .identifier(34477307L)
                        .familyName("Doe")
                        .givenName("John")
                        .gender(Gender.MALE)
                        .encounters(new ArrayList<>())
                        .birthDate(new Date())
                        .build();
        ReflectionTestUtils.setField(testPatient, "id", 1L);
        ReflectionTestUtils.setField(testPatient, "softDelete", false);
        encounter =
                Encounter.builder()
                        .start(LocalDateTime.of(2025, 11, 1, 10, 0))
                        .end(LocalDateTime.of(2025, 11, 1, 11, 0))
                        .patient(testPatient)
                        .observations(new ArrayList<>())
                        .build();
        ReflectionTestUtils.setField(encounter, "id", 1L);
        ReflectionTestUtils.setField(encounter, "softDelete", false);
        family = "Doe";
        givenName = "John";
        identifier = 12345L;
        birthDate = "2025-11-01";
        testObservation =
                Observation.builder()
                        .code("BP-001")
                        .value("120/80")
                        .effectiveDateTime(LocalDateTime.of(2024, 1, 15, 10, 30))
                        .patient(testPatient)
                        .build();
        ReflectionTestUtils.setField(testObservation, "id", 1L);
        ReflectionTestUtils.setField(testObservation, "softDelete", false);
        testObservationWrapper = new ObservationWrapper("BP-001", "120/80", "2024-01-15 10:30:00");
    }

    @Test
    @DisplayName("Create a patient successfully")
    void createPatient() {
        PatientWrapper patientWrapper =
                PatientWrapper.builder()
                        .identifier(34477307L)
                        .gender("MALE")
                        .familyName("Doe")
                        .givenName("John")
                        .birthDate("1994-01-01")
                        .build();
        when(patientRepository.findByIdentifier(34477307L)).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        UniversalResponse response = patientServiceImpl.createPatient(patientWrapper);
        verify(patientRepository, times(1)).findByIdentifier(34477307L);
        verify(patientRepository, times(1)).save(any(Patient.class));
        assertEquals(200, response.status());
        assertEquals("Patient created successfully", response.message());
        assertNotNull(response.data());
    }

    @Test
    @DisplayName("Retrieve a patient successfully")
    void retrievePatient() {
        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        UniversalResponse response = patientServiceImpl.retrievePatient(patientId);
        verify(patientRepository, times(1)).findByIdAndSoftDeleteFalse(patientId);
        assertEquals(200, response.status());
        assertEquals(1L, testPatient.getId());
        assertEquals("Patient retrieved successfully", response.message());
        assertNotNull(response.data());
    }

    @Test
    @DisplayName("Update patient record successfully")
    void updatePatient() {
        PatientWrapper patientWrapper =
                PatientWrapper.builder()
                        .identifier(34477317L)
                        .gender("MALE")
                        .familyName("Felix")
                        .givenName("Maina")
                        .birthDate("1994-01-01")
                        .build();
        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        UniversalResponse response = patientServiceImpl.updatePatient(patientId, patientWrapper);
        verify(patientRepository, times(1)).findByIdAndSoftDeleteFalse(patientId);
        assertEquals(200, response.status());
        assertEquals(1L, testPatient.getId());
        assertEquals("Patient updated successfully", response.message());
        assertNotNull(response.data());
    }

    @Test
    @DisplayName(" delete patient without encounter")
    void deletePatient() {
        testPatient.setEncounters(new ArrayList<>());
        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        UniversalResponse response = patientServiceImpl.deletePatient(patientId);
        verify(patientRepository, times(1)).findByIdAndSoftDeleteFalse(patientId);
        verify(patientRepository, times(1)).save(any(Patient.class));
        assertEquals(200, response.status());
        assertEquals("Patient deleted successfully", response.message());
    }

    @Test
    @DisplayName("Delete patient with encounters")
    void deletePatientWithEncounters() {
        testPatient.setSoftDelete(false);
        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        when(encounterRepository.countByPatientIdAndSoftDeleteFalse(patientId)).thenReturn(1L);
        PatientException exception =
                assertThrows(
                        PatientException.class, () -> patientServiceImpl.deletePatient(patientId));
        assertEquals("Patient has encounters, kindly clear the encounters", exception.getMessage());
        assertFalse(testPatient.getSoftDelete());
        verify(patientRepository, times(1)).findByIdAndSoftDeleteFalse(patientId);
        verify(encounterRepository, times(1)).countByPatientIdAndSoftDeleteFalse(patientId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Add patient encounter")
    void addPatientEncounters() {
        Long patientId = 1L;
        Patient testPatient = new Patient();
        EncounterWrapper encounterWrapper =
                EncounterWrapper.builder()
                        .start("2025-11-01 10:30:15")
                        .encounterDate("2025-11-01")
                        .build();

        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        when(encounterRepository.save(any(Encounter.class)))
                .thenAnswer(
                        invocation -> {
                            Encounter saved = invocation.getArgument(0);
                            ReflectionTestUtils.setField(saved, "id", 1L);
                            return saved;
                        });

        UniversalResponse response =
                patientServiceImpl.addPatientEncounters(patientId, encounterWrapper);
        assertEquals("Encounter added successfully", response.message());
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertInstanceOf(Encounter.class, response.data());
        assertEquals(1L, ((Encounter) response.data()).getId());
        verify(patientRepository).save(any(Patient.class));
        verify(encounterRepository).save(any(Encounter.class));
    }

    @Test
    @DisplayName("end patient encounter")
    void endPatientEncounter() {
        Long encounterId = 999L;
        String endEncounter = "2025-11-01 11:30:00";
        when(encounterRepository.findByIdAndSoftDeleteFalse(encounterId))
                .thenReturn(Optional.empty());
        PatientException exception =
                assertThrows(
                        PatientException.class,
                        () -> patientServiceImpl.endPatientEncounter(encounterId, endEncounter));
        assertEquals("Encounter with the given id does not exist", exception.getMessage());
        verify(encounterRepository, never()).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should retrieve patient encounters and observations with pagination successfully")
    void retrievePatientEncountersAndObservation_Success() {
        int page = 0;
        int size = 10;
        Encounter firstEncounter =
                createEncounter(1L, testPatient, LocalDateTime.now().minusDays(1));
        Encounter secondEncounter =
                createEncounter(2L, testPatient, LocalDateTime.now().minusDays(3));
        List<Encounter> encounters = Arrays.asList(firstEncounter, secondEncounter);
        Page<Encounter> encounterPage = new PageImpl<>(encounters, PageRequest.of(page, size), 2);
        when(patientRepository
                        .findPatientByFamilyNameAndGivenNameAndIdentifierAndBirthDateAndSoftDeleteFalse(
                                family, givenName, identifier, formatDate(birthDate)))
                .thenReturn(Optional.of(testPatient));
        when(encounterRepository.findByPatientIdAndSoftDeleteFalse(
                        patientId, PageRequest.of(page, size)))
                .thenReturn(encounterPage);
        UniversalResponse response =
                patientServiceImpl.retrievePatientEncountersAndObservation(
                        family, givenName, identifier, birthDate, page, size);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("Patient encounters and Observations", response.message());
        assertNotNull(response.data());
        Map<String, Object> responseData = (Map<String, Object>) response.data();
        assertEquals(2, ((List<?>) responseData.get("encounters")).size());
        assertEquals(0, responseData.get("currentPage"));
        assertEquals(1, responseData.get("totalPages"));
        assertEquals(2L, responseData.get("totalEncounters"));
        assertEquals(false, responseData.get("hasNext"));
        assertEquals(false, responseData.get("hasPrevious"));
        verify(patientRepository, times(1))
                .findPatientByFamilyNameAndGivenNameAndIdentifierAndBirthDateAndSoftDeleteFalse(
                        family, givenName, identifier, formatDate(birthDate));
        verify(encounterRepository, times(1))
                .findByPatientIdAndSoftDeleteFalse(patientId, PageRequest.of(page, size));
    }

    private Encounter createEncounter(Long id, Patient patient, LocalDateTime startTime) {
        Encounter encounter =
                Encounter.builder()
                        .patient(patient)
                        .start(startTime)
                        .encounterDate(new Date())
                        .observations(new ArrayList<>())
                        .build();

        ReflectionTestUtils.setField(encounter, "id", id);
        ReflectionTestUtils.setField(encounter, "softDelete", false);
        return encounter;
    }

    public Date formatDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        } catch (Exception exception) {
            throw new PatientException(exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should retrieve patient encounters successfully when encounters exist")
    void viewPatientEncounters_Success_WithEncounters() {
        Encounter encounter1 = createEncounter(1L, testPatient, LocalDateTime.now().minusDays(1));
        Encounter encounter2 = createEncounter(2L, testPatient, LocalDateTime.now().minusDays(2));
        Encounter encounter3 = createEncounter(3L, testPatient, LocalDateTime.now().minusDays(3));
        List<Encounter> encounters = Arrays.asList(encounter1, encounter2, encounter3);
        testPatient.setEncounters(encounters);
        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        UniversalResponse response = patientServiceImpl.viewPatientEncounters(patientId);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("Patient encounters retrieved successfully", response.message());
        assertNotNull(response.data());
        List<Encounter> returnedEncounters = (List<Encounter>) response.data();
        assertEquals(3, returnedEncounters.size());
        assertEquals(encounter1.getId(), returnedEncounters.get(0).getId());
        assertEquals(encounter2.getId(), returnedEncounters.get(1).getId());
        assertEquals(encounter3.getId(), returnedEncounters.get(2).getId());
        verify(patientRepository, times(1)).findByIdAndSoftDeleteFalse(patientId);
    }

    @Test
    @DisplayName("Should retrieve patient observations successfully when observations exist")
    void viewPatientObservations_Success_WithObservations() {
        Observation obs1 =
                createObservation(
                        1L,
                        "BP",
                        "120/80",
                        testPatient,
                        encounter,
                        LocalDateTime.now().minusDays(1));
        Observation obs2 =
                createObservation(
                        2L,
                        "Temperature",
                        "37.5",
                        testPatient,
                        encounter,
                        LocalDateTime.now().minusDays(2));
        Observation obs3 =
                createObservation(
                        3L,
                        "Heart Rate",
                        "75",
                        testPatient,
                        encounter,
                        LocalDateTime.now().minusDays(3));
        List<Observation> observations = Arrays.asList(obs1, obs2, obs3);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Observation> observationsPage = new PageImpl<>(observations, pageable, 3);

        when(patientRepository.findByIdAndSoftDeleteFalse(patientId))
                .thenReturn(Optional.of(testPatient));
        when(observationRepository.findObservationByPatientAndSoftDeleteFalse(
                        testPatient, pageable))
                .thenReturn(observationsPage);
        UniversalResponse response = patientServiceImpl.viewPatientObservations(patientId);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("Patient observations retrieved successfully", response.message());
        assertNotNull(response.data());
        List<Observation> returnedObservations = (List<Observation>) response.data();
        assertEquals(3, returnedObservations.size());
    }

    @Test
    @DisplayName("add observations to an existing encounter")
    void addEncounterObservationWhenEncounterExists() {
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class))).thenReturn(testObservation);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        UniversalResponse response =
                patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        assertNotNull(response);
        assertEquals("Encounter added successfully", response.message());
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals(encounter, response.data());
        assertEquals(1, encounter.getObservations().size());
        verify(encounterRepository).findByIdAndSoftDeleteFalse(1L);
        verify(observationRepository).save(any(Observation.class));
        verify(encounterRepository).save(encounter);
    }

    @Test
    @DisplayName("throw exception when adding an observation to a  non existing encounter")
    void addEncounterObservationWhenEncounterNotFound_ShouldThrowPatientException() {
        when(encounterRepository.findByIdAndSoftDeleteFalse(anyLong()))
                .thenReturn(Optional.empty());
        PatientException exception =
                assertThrows(
                        PatientException.class,
                        () -> {
                            patientServiceImpl.addEncounterObservation(
                                    999L, testObservationWrapper);
                        });
        assertEquals("Encounter with the given id does not exist", exception.getMessage());
        verify(encounterRepository).findByIdAndSoftDeleteFalse(999L);
        verify(observationRepository, never()).save(any());
        verify(encounterRepository, never()).save(any());
    }

    public Observation createObservation(
            Long id,
            String code,
            String value,
            Patient patient,
            Encounter encounter,
            LocalDateTime effectiveDateTime) {
        Observation observation =
                Observation.builder()
                        .code(code)
                        .effectiveDateTime(effectiveDateTime)
                        .encounter(encounter)
                        .patient(patient)
                        .value(value)
                        .build();
        ReflectionTestUtils.setField(encounter, "id", id);
        ReflectionTestUtils.setField(encounter, "softDelete", false);
        return observation;
    }

    @Test
    @DisplayName("Should initialize list when encounter observation is null")
    void addEncounterObservation_WhenObservationsListIsNull_ShouldInitializeList() {
        encounter.setObservations(null);
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class))).thenReturn(testObservation);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        UniversalResponse response =
                patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        assertNotNull(response);
        assertNotNull(encounter.getObservations());
        assertEquals(1, encounter.getObservations().size());
        verify(encounterRepository).save(encounter);
    }

    @Test
    @DisplayName("Should append an observation to an existing observation list")
    void addEncounterObservation_WhenObservationsListExists_ShouldAppendToExistingList() {
        Observation existingObservation =
                Observation.builder().code("TEMP-001").value("98.6").build();
        encounter.getObservations().add(existingObservation);
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class))).thenReturn(testObservation);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        assertEquals(2, encounter.getObservations().size());
        assertTrue(encounter.getObservations().contains(existingObservation));
    }

    @Test
    @DisplayName("Should update observation properties correctly")
    void addEncounterObservation_ShouldSetCorrectObservationProperties() {
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        verify(observationRepository)
                .save(
                        argThat(
                                observation ->
                                        observation.getCode().equals("BP-001")
                                                && observation.getValue().equals("120/80")
                                                && observation.getPatient().equals(testPatient)
                                                && observation.getEffectiveDateTime() != null));
    }

    @Test
    @DisplayName("Should format date and time when adding observation ")
    void addEncounterObservation_ShouldCallFormatDateTime() {
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class))).thenReturn(testObservation);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        verify(observationRepository)
                .save(argThat(observation -> observation.getEffectiveDateTime() != null));
    }

    @Test
    @DisplayName("Should save observation before adding to an encounter")
    void addEncounterObservation_ShouldSaveObservationBeforeAddingToEncounter() {
        when(encounterRepository.findByIdAndSoftDeleteFalse(1L)).thenReturn(Optional.of(encounter));
        when(observationRepository.save(any(Observation.class))).thenReturn(testObservation);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);
        patientServiceImpl.addEncounterObservation(1L, testObservationWrapper);
        var inOrder = inOrder(observationRepository, encounterRepository);
        inOrder.verify(observationRepository).save(any(Observation.class));
        inOrder.verify(encounterRepository).save(encounter);
    }

    @Test
    @DisplayName("Should throw exception when retrieving non-existent patient")
    void retrievePatient_WhenNotFound_ShouldThrowException() {
        when(patientRepository.findByIdAndSoftDeleteFalse(999L)).thenReturn(Optional.empty());
        assertThrows(PatientException.class, () -> patientServiceImpl.retrievePatient(999L));
        verify(patientRepository).findByIdAndSoftDeleteFalse(999L);
    }

    @Test
    @DisplayName("Should restore soft-deleted patient when creating with same identifier")
    void createPatient_WhenSoftDeleted_ShouldRestore() {
        PatientWrapper patientWrapper =
                PatientWrapper.builder()
                        .identifier(12345678L)
                        .gender("FEMALE")
                        .familyName("Restored")
                        .givenName("Patient")
                        .birthDate("1990-01-01")
                        .build();

        Patient softDeletedPatient =
                Patient.builder()
                        .identifier(12345678L)
                        .gender(Gender.MALE)
                        .familyName("Old")
                        .givenName("Patient")
                        .build();
        ReflectionTestUtils.setField(softDeletedPatient, "id", 1L);
        ReflectionTestUtils.setField(softDeletedPatient, "softDelete", true);

        when(patientRepository.findByIdentifier(12345678L))
                .thenReturn(Optional.of(softDeletedPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(softDeletedPatient);

        UniversalResponse response = patientServiceImpl.createPatient(patientWrapper);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("Patient restored and updated successfully", response.message());
        assertFalse(softDeletedPatient.getSoftDelete());
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate identifier")
    void updatePatient_WhenDuplicateIdentifier_ShouldThrowException() {
        PatientWrapper patientWrapper =
                PatientWrapper.builder()
                        .identifier(99999999L)
                        .gender("MALE")
                        .familyName("Test")
                        .givenName("Patient")
                        .birthDate("1990-01-01")
                        .build();

        Patient existingPatient =
                Patient.builder()
                        .identifier(88888888L)
                        .gender(Gender.MALE)
                        .familyName("Existing")
                        .givenName("Patient")
                        .build();
        ReflectionTestUtils.setField(existingPatient, "id", 1L);
        ReflectionTestUtils.setField(existingPatient, "softDelete", false);

        Patient duplicatePatient =
                Patient.builder().identifier(99999999L).gender(Gender.MALE).build();
        ReflectionTestUtils.setField(duplicatePatient, "id", 2L);

        when(patientRepository.findByIdAndSoftDeleteFalse(1L))
                .thenReturn(Optional.of(existingPatient));
        when(patientRepository.findByIdentifierAndSoftDeleteFalse(99999999L))
                .thenReturn(Optional.of(duplicatePatient));

        assertThrows(
                PatientException.class, () -> patientServiceImpl.updatePatient(1L, patientWrapper));
    }

    @Test
    @DisplayName("Should successfully end patient encounter")
    void endPatientEncounter_WhenValid_ShouldSucceed() {
        Encounter openEncounter =
                Encounter.builder()
                        .start(LocalDateTime.now().minusHours(2))
                        .patient(testPatient)
                        .build();
        ReflectionTestUtils.setField(openEncounter, "id", 1L);
        ReflectionTestUtils.setField(openEncounter, "softDelete", false);

        when(encounterRepository.findByIdAndSoftDeleteFalse(1L))
                .thenReturn(Optional.of(openEncounter));
        when(encounterRepository.save(any(Encounter.class))).thenReturn(openEncounter);

        String endTime =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        UniversalResponse response = patientServiceImpl.endPatientEncounter(1L, endTime);

        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("Encounter ended successfully", response.message());
        assertNotNull(response.data());
        verify(encounterRepository).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should throw exception for null date format")
    void formatDate_WhenNull_ShouldThrowException() {
        assertThrows(
                PatientException.class,
                () ->
                        ReflectionTestUtils.invokeMethod(
                                patientServiceImpl, "formatDate", (String) null));
    }

    @Test
    @DisplayName("Should throw exception for invalid date format")
    void formatDate_WhenInvalidFormat_ShouldThrowException() {
        assertThrows(
                PatientException.class,
                () ->
                        ReflectionTestUtils.invokeMethod(
                                patientServiceImpl, "formatDate", "invalid-date"));
    }

    @Test
    @DisplayName("Should throw exception for invalid datetime format")
    void formatDateTime_WhenInvalidFormat_ShouldThrowException() {
        assertThrows(
                PatientException.class,
                () ->
                        ReflectionTestUtils.invokeMethod(
                                patientServiceImpl, "formatDateTime", "invalid-datetime"));
    }
}
