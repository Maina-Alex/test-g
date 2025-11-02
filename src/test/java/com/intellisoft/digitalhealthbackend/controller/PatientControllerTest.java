package com.intellisoft.digitalhealthbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellisoft.digitalhealthbackend.dto.EncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.EndEncounterWrapper;
import com.intellisoft.digitalhealthbackend.dto.ObservationWrapper;
import com.intellisoft.digitalhealthbackend.dto.PatientWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "api-client", roles = {"API_CLIENT"})
    @DisplayName("Create a patient successfully")
    void createPatient_ShouldReturnCreatedPatient() throws Exception {
        PatientWrapper patientWrapper = createSamplePatientWrapper();
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient created successfully"))).andExpect(jsonPath("$.data").exists());
    }


    @Test
    @DisplayName("Retrieve a patient successfully")
    void retrievePatient_WithValidId_ShouldReturnPatient() throws Exception {
        Long patientId = 1L;
        mockMvc.perform(get("/api/patients/{id}", patientId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is( "Patient retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Retrieve a patient with invalid id")
    void retrievePatient_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(get("/api/patients/{id}", invalidPatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    @DisplayName("Update patient record successfully")
    void updatePatient_WithValidData_ShouldReturnUpdatedPatient() throws Exception {
        Long patientId = 1L;
        PatientWrapper updatedPatient = createSamplePatientWrapper();
        mockMvc.perform(put("/api/patients/{id}", patientId).contentType(MediaType.APPLICATION_JSON)
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
        mockMvc.perform(put("/api/patients/{id}", invalidPatientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    @DisplayName("Delete patient successfully")
    void deletePatient_WithValidId_ShouldReturnSuccess() throws Exception {
        Long patientId = 1L;
        mockMvc.perform(delete("/api/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient deleted successfully")));
    }
    @Test
    @DisplayName("Throw error if patient have existing encounters")
    void deletePatient_WithVExistingEncounters_ShouldThrowError() throws Exception {
        Long patientId = 1L;
        mockMvc.perform(delete("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient has encounters, kindly clear the encounters")));
    }
    @Test
    @DisplayName("Delete patient with invalid id")
    void deletePatient_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(delete("/api/patients/{id}", invalidPatientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    @DisplayName("Add patient encounter successfully")
    void addPatientEncounter_WithValidData_ShouldReturnSuccess() throws Exception {
        Long patientId = 1L;
        EncounterWrapper encounterWrapper = createSampleEncounterWrapper();
        mockMvc.perform(post("/api/patients/add-encounter/{patientId}", patientId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter added successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("Add patient encounter with invalid patient id")
    void addPatientEncounter_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        EncounterWrapper encounterWrapper = createSampleEncounterWrapper();
        mockMvc.perform(post("/api/patients/add-encounter/{patientId}", invalidPatientId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    void addEncounterObservation_WithValidData_ShouldReturnSuccess() throws Exception {
        Long encounterId = 2L;
        ObservationWrapper observationWrapper = createSampleObservationWrapper();
        mockMvc.perform(post("/api/patients/add/observations/{encounterId}", encounterId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(observationWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter added successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void addEncounterObservation_WithInvalidEncounterId_ShouldReturnError() throws Exception {
        Long invalidEncounterId = 99999L;
        ObservationWrapper observationWrapper = createSampleObservationWrapper();
        mockMvc.perform(post("/api/patients/add/observations/{encounterId}", invalidEncounterId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(observationWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter with the given id does not exist")));
    }

    @Test
    void endPatientEncounter_WithValidData_ShouldReturnSuccess() throws Exception {
        Long encounterId = 2L;
        EndEncounterWrapper encounterWrapper = createEncounterWrapper();
        mockMvc.perform(post("/api/patients/end/encounter/{encounterId}", encounterId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter ended successfully")));
    }

    @Test
    @DisplayName("End patient encounter with invalid encounter id")
    void endPatientEncounter_WithInvalidEncounterId_ShouldReturnError() throws Exception {
        Long invalidEncounterId = 99999L;
        EndEncounterWrapper encounterWrapper = createEncounterWrapper();
        mockMvc.perform(post("/api/patients/end/encounter/{encounterId}", invalidEncounterId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(encounterWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Encounter with the given id does not exist")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void retrievePatientEncountersAndObservation_WithValidParams_ShouldReturnResults() throws Exception {
        String family = "Maina";
        String givenName = "Felix";
        Long identifier = 12345678L;
        String birthDate = "1990-01-01";
        int page = 0;
        int size = 10;
        mockMvc.perform(get("/api/patients").param("family", family).param("given", givenName).param("identifier", String.valueOf(identifier)).param("birthDate", birthDate).param("page", String.valueOf(page)).param("size", String.valueOf(size)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient encounters and Observations")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void retrievePatientEncountersAndObservation_WithMissingParams_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void viewPatientEncounters_WithValidPatientId_ShouldReturnEncounters() throws Exception {
        Long patientId = 1L;
        mockMvc.perform(get("/api/patients/{id}/encounters", patientId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient encounters retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void viewPatientEncounters_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(get("/api/patients/{id}/encounters", invalidPatientId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    @Test
    void viewPatientObservations_WithValidPatientId_ShouldReturnObservations() throws Exception {
        Long patientId = 1L;
        mockMvc.perform(get("/api/patients/{id}/observations", patientId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient observations retrieved successfully")))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void viewPatientObservations_WithInvalidPatientId_ShouldReturnError() throws Exception {
        Long invalidPatientId = 99999L;
        mockMvc.perform(get("/api/patients/{id}/observations", invalidPatientId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Patient not found")));
    }

    public PatientWrapper createSamplePatientWrapper() {
        return PatientWrapper.builder().birthDate("1996-08-09").identifier(16372916L).familyName("Maina").givenName("Felix").gender("MALE").build();

    }

    private EncounterWrapper createSampleEncounterWrapper() {
        return EncounterWrapper.builder()
                .start("2025-11-01 10:30:15")
                .encounterDate("2025-11-01")
                .patient(1L).build();

    }

    private ObservationWrapper createSampleObservationWrapper() {
        return ObservationWrapper.builder().code("BP-01").value("120/190").effectiveDateTime("2025-11-01 11:30:15").build();
    }
    private EndEncounterWrapper createEncounterWrapper() {
        return EndEncounterWrapper.builder().endEncounter("2025-11-01 11:30:15").build();
    }

}