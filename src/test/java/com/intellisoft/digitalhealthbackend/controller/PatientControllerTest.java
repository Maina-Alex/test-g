package com.intellisoft.digitalhealthbackend.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellisoft.digitalhealthbackend.dto.EncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.EndEncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.ObservationWrapper;
import com.intellisoft.digitalhealthbackend.dto.PatientWrapper;
import com.intellisoft.digitalhealthbackend.enums.Gender;
import com.intellisoft.digitalhealthbackend.models.Encounter;
import com.intellisoft.digitalhealthbackend.models.Patient;
import com.intellisoft.digitalhealthbackend.repository.EncounterRepository;
import com.intellisoft.digitalhealthbackend.repository.PatientRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PatientRepository patientRepository;
    @Autowired private EncounterRepository encounterRepository;

    @Value("${api.key.header:X-API-KEY}")
    private String apiKeyHeader;

    @Value("${api.key.secret:367293648}")
    private String apiKeyValue;

    @Test
    @DisplayName("Create a patient successfully")
    void createPatient_ShouldReturnCreatedPatient() throws Exception {
        PatientWrapper patientWrapper = createSamplePatientWrapper();
        mockMvc.perform(
                        post("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patientWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient created successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Create patient with duplicate identifier should fail")
    void createPatient_WithDuplicateIdentifier_ShouldFail() throws Exception {
        // First create a patient with identifier 12345678L
        Patient existingPatient =
                Patient.builder()
                        .identifier(12345678L)
                        .givenName("Original")
                        .familyName("Patient")
                        .gender(Gender.MALE)
                        .birthDate(parseDate("1990-01-15"))
                        .build();
        patientRepository.save(existingPatient);

        // Now try to create another patient with the same identifier
        PatientWrapper patientWrapper =
                PatientWrapper.builder()
                        .birthDate("1990-01-15")
                        .identifier(12345678L)
                        .familyName("Test")
                        .givenName("Duplicate")
                        .gender("MALE")
                        .build();
        mockMvc.perform(
                        post("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patientWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Patient already exists"));
    }

    @Test
    @DisplayName("Retrieve a patient successfully")
    void retrievePatient_WithValidId_ShouldReturnPatient() throws Exception {
        Long patientId = createTestPatient();
        mockMvc.perform(
                        get("/api/patients/{id}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Retrieve a patient with invalid id")
    void retrievePatient_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(
                        get("/api/patients/{id}", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    @DisplayName("Update patient record successfully")
    void updatePatient_WithValidData_ShouldReturnUpdatedPatient() throws Exception {
        Long patientId = createTestPatient();
        PatientWrapper updatedPatient = createSamplePatientWrapper();
        mockMvc.perform(
                        put("/api/patients/{id}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient updated successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Update patient record with invalid id")
    void updatePatient_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidPatientId = 99999L;
        PatientWrapper patientWrapper = createSamplePatientWrapper();
        mockMvc.perform(
                        put("/api/patients/{id}", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patientWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    @DisplayName("Delete patient successfully")
    void deletePatient_WithValidId_ShouldReturnSuccess() throws Exception {
        Long patientId = createTestPatient();
        mockMvc.perform(
                        delete("/api/patients/{id}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient deleted successfully")));
    }

    @Test
    @DisplayName("Throw error if patient have existing encounters")
    void deletePatient_WithVExistingEncounters_ShouldThrowError() throws Exception {
        Long patientId = createTestPatientWithEncounter();
        mockMvc.perform(
                        delete("/api/patients/{id}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Patient has encounters, kindly clear the encounters")));
    }

    @Test
    @DisplayName("Delete patient with invalid id")
    void deletePatient_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(
                        delete("/api/patients/{id}", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    @DisplayName("Add patient encounter successfully")
    void addPatientEncounter_WithValidData_ShouldReturnSuccess() throws Exception {
        Long patientId = createTestPatient();
        EncounterWrapper encounterWrapper = createSampleEncounterWrapper();
        mockMvc.perform(
                        post("/api/patients/add-encounter/{patientId}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter added successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Add patient encounter with invalid patient id")
    void addPatientEncounter_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        EncounterWrapper encounterWrapper = createSampleEncounterWrapper();
        mockMvc.perform(
                        post("/api/patients/add-encounter/{patientId}", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    void addEncounterObservation_WithValidData_ShouldReturnSuccess() throws Exception {
        Long encounterId = createTestEncounter();
        ObservationWrapper observationWrapper = createSampleObservationWrapper();
        mockMvc.perform(
                        post("/api/patients/add/observations/{encounterId}", encounterId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(observationWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter added successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void addEncounterObservation_WithInvalidEncounterId_ShouldReturnError() throws Exception {
        Long invalidEncounterId = 99999L;
        ObservationWrapper observationWrapper = createSampleObservationWrapper();
        mockMvc.perform(
                        post("/api/patients/add/observations/{encounterId}", invalidEncounterId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(observationWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter with the given id does not exist")));
    }

    @Test
    void endPatientEncounter_WithValidData_ShouldReturnSuccess() throws Exception {
        Long encounterId = createTestEncounter();
        EndEncounterWrapper encounterWrapper = createEncounterWrapper();
        mockMvc.perform(
                        post("/api/patients/end/encounter/{encounterId}", encounterId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter ended successfully")));
    }

    @Test
    @DisplayName("End patient encounter with invalid encounter id")
    void endPatientEncounter_WithInvalidEncounterId_ShouldReturnError() throws Exception {
        Long invalidEncounterId = 99999L;
        EndEncounterWrapper encounterWrapper = createEncounterWrapper();
        mockMvc.perform(
                        post("/api/patients/end/encounter/{encounterId}", invalidEncounterId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter with the given id does not exist")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void retrievePatientEncountersAndObservation_WithValidParams_ShouldReturnResults()
            throws Exception {
        // Create a patient with the exact query parameters
        String family = "Maina";
        String givenName = "Felix";
        Long identifier = 12345678L;
        String birthDate = "1990-01-01";
        Patient patient =
                Patient.builder()
                        .identifier(identifier)
                        .givenName(givenName)
                        .familyName(family)
                        .gender(Gender.MALE)
                        .birthDate(parseDate(birthDate))
                        .build();
        patientRepository.save(patient);

        int page = 0;
        int size = 10;
        mockMvc.perform(
                        get("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .param("family", family)
                                .param("given", givenName)
                                .param("identifier", String.valueOf(identifier))
                                .param("birthDate", birthDate)
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient encounters and Observations")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void retrievePatientEncountersAndObservation_WithMissingParams_ShouldReturnBadRequest()
            throws Exception {
        mockMvc.perform(
                        get("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void viewPatientEncounters_WithValidPatientId_ShouldReturnEncounters() throws Exception {
        Long patientId = createTestPatient();
        mockMvc.perform(
                        get("/api/patients/{id}/encounters", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient encounters retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void viewPatientEncounters_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(
                        get("/api/patients/{id}/encounters", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    void viewPatientObservations_WithValidPatientId_ShouldReturnObservations() throws Exception {
        Long patientId = createTestPatient();
        mockMvc.perform(
                        get("/api/patients/{id}/observations", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient observations retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void viewPatientObservations_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(
                        get("/api/patients/{id}/observations", invalidPatientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    @DisplayName("Retrieve patient encounters with pagination")
    void retrievePatientEncounters_WithPagination_ShouldReturnPaginatedResults() throws Exception {
        // Create a patient with the exact query parameters
        String family = "Doe";
        String givenName = "John";
        Long identifier =
                ThreadLocalRandom.current()
                        .nextLong(10000000L, 99999999L); // Use unique identifier to avoid conflicts
        String birthDate = "1990-01-15";
        Patient patient =
                Patient.builder()
                        .identifier(identifier)
                        .givenName(givenName)
                        .familyName(family)
                        .gender(Gender.MALE)
                        .birthDate(parseDate(birthDate))
                        .build();
        patientRepository.save(patient);

        mockMvc.perform(
                        get("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .param("family", family)
                                .param("given", givenName)
                                .param("identifier", String.valueOf(identifier))
                                .param("birthDate", birthDate)
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.hasNext").exists())
                .andExpect(jsonPath("$.data.hasPrevious").exists());
    }

    @Test
    @DisplayName("Retrieve patient not found should return error")
    void retrievePatient_NotFound_ShouldReturnError() throws Exception {
        mockMvc.perform(
                        get("/api/patients")
                                .header(apiKeyHeader, apiKeyValue)
                                .param("family", "NonExistent")
                                .param("given", "Patient")
                                .param("identifier", "99999999")
                                .param("birthDate", "2000-01-01")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    @DisplayName("Update patient with same identifier should succeed")
    void updatePatient_WithSameIdentifier_ShouldSucceed() throws Exception {
        // Create a patient with identifier 12345678L
        Patient patient =
                Patient.builder()
                        .identifier(12345678L)
                        .givenName("Original")
                        .familyName("Name")
                        .gender(Gender.MALE)
                        .birthDate(parseDate("1990-01-15"))
                        .build();
        Patient savedPatient = patientRepository.save(patient);
        Long patientId = savedPatient.getId();

        PatientWrapper updatedPatient =
                PatientWrapper.builder()
                        .birthDate("1990-01-15")
                        .identifier(12345678L)
                        .familyName("Updated")
                        .givenName("Name")
                        .gender("MALE")
                        .build();
        mockMvc.perform(
                        put("/api/patients/{id}", patientId)
                                .header(apiKeyHeader, apiKeyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient updated successfully")));
    }

    public PatientWrapper createSamplePatientWrapper() {
        return PatientWrapper.builder()
                .birthDate("1996-08-09")
                .identifier(16372916L)
                .familyName("Maina")
                .givenName("Felix")
                .gender("MALE")
                .build();
    }

    private EncounterWrapper createSampleEncounterWrapper() {
        return EncounterWrapper.builder()
                .start("2025-11-01 10:30:15")
                .encounterDate("2025-11-01")
                .patient(1L)
                .build();
    }

    private ObservationWrapper createSampleObservationWrapper() {
        return ObservationWrapper.builder()
                .code("BP-01")
                .value("120/190")
                .effectiveDateTime("2025-11-01 11:30:15")
                .build();
    }

    private EndEncounterWrapper createEncounterWrapper() {
        return EndEncounterWrapper.builder().endEncounter("2025-11-01 11:30:15").build();
    }

    private Long createTestPatient() throws ParseException {
        // Generate a unique identifier to avoid conflicts
        Long identifier = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        Patient patient =
                Patient.builder()
                        .identifier(identifier)
                        .givenName("Test")
                        .familyName("Patient")
                        .gender(Gender.MALE)
                        .birthDate(parseDate("1990-01-15"))
                        .build();
        Patient savedPatient = patientRepository.save(patient);
        return savedPatient.getId();
    }

    private Long createTestPatientWithEncounter() throws ParseException {
        // Generate a unique identifier to avoid conflicts
        Long identifier = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        Patient patient =
                Patient.builder()
                        .identifier(identifier)
                        .givenName("Test")
                        .familyName("Patient")
                        .gender(Gender.MALE)
                        .birthDate(parseDate("1990-01-15"))
                        .build();
        Patient savedPatient = patientRepository.save(patient);

        Encounter encounter =
                Encounter.builder()
                        .patient(savedPatient)
                        .encounterDate(parseDate("2025-11-01"))
                        .start(LocalDateTime.parse("2025-11-01T10:30:15"))
                        .build();
        encounterRepository.save(encounter);

        return savedPatient.getId();
    }

    private Long createTestEncounter() throws ParseException {
        // First create a patient
        Long identifier = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        Patient patient =
                Patient.builder()
                        .identifier(identifier)
                        .givenName("Test")
                        .familyName("Patient")
                        .gender(Gender.MALE)
                        .birthDate(parseDate("1990-01-15"))
                        .build();
        Patient savedPatient = patientRepository.save(patient);

        Encounter encounter =
                Encounter.builder()
                        .patient(savedPatient)
                        .encounterDate(parseDate("2025-11-01"))
                        .start(LocalDateTime.parse("2025-11-01T10:30:15"))
                        .build();
        Encounter savedEncounter = encounterRepository.save(encounter);

        return savedEncounter.getId();
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        return formatter.parse(date);
    }
}
