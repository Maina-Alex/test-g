package com.intellisoft.digitalhealthbackend.controller;

import com.intellisoft.digitalhealthbackend.dto.*;
import com.intellisoft.digitalhealthbackend.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/patients")
@RestController
public class PatientController {
    private  final PatientService patientService;
    @Operation(description = "Creates Patient")
    @PostMapping()
    public ResponseEntity<UniversalResponse>createPatient (@RequestBody PatientWrapper patientWrapper){
        return ResponseEntity.ok(patientService.createPatient(patientWrapper));
    }
    @Operation(summary = "Retrieve existing Patient")
    @GetMapping("/{id}")
    public ResponseEntity<UniversalResponse>retrievePatient(@PathVariable(name = "id") Long patientId){
        return ResponseEntity.ok(patientService.retrievePatient(patientId));
    }
    @Operation(summary = "Update patient")
    @PutMapping("/{id}")
    public ResponseEntity<UniversalResponse>updatePatient(@PathVariable(name = "id") Long patientId, @RequestBody PatientWrapper patientWrapper){
        return ResponseEntity.ok(patientService.updatePatient(patientId, patientWrapper));
    }
    @Operation(summary = "Delete patient")
    @DeleteMapping("/{id}")
    public ResponseEntity<UniversalResponse>deletePatient(@PathVariable(name = "id") Long patientId){
        return ResponseEntity.ok(patientService.deletePatient(patientId));
    }
    @Operation(summary = "add patient encounter")
    @PostMapping("add-encounter/{patientId}")
    public ResponseEntity<UniversalResponse>addPatientEncounter(@PathVariable(name = "patientId") Long patientId, @RequestBody EncounterWrapper encounterWrapper){
        return ResponseEntity.ok(patientService.addPatientEncounters(patientId,encounterWrapper));
    }
    @Operation(summary = "add encounter Observations")
    @PostMapping("add/observations/{encounterId}")
    public ResponseEntity<UniversalResponse>addEncounterObservation(@PathVariable(name = "encounterId") Long encounterId, @RequestBody ObservationWrapper observationWrapper){
        return ResponseEntity.ok(patientService.addEncounterObservation(encounterId,observationWrapper));
    }
    @PostMapping("end/encounter/{encounterId}")
    @Operation(summary = "End patient encounter")
    public ResponseEntity<UniversalResponse>endPatientEncounter(@PathVariable(name = "encounterId") Long encounterId, @RequestBody EndEncounterWrapper endEncounter){
        return ResponseEntity.ok(patientService.endPatientEncounter(encounterId,endEncounter.endEncounter()));
    }

    @GetMapping()
    @Operation(summary = "Retrieve patient encounter and observations")
    public ResponseEntity<UniversalResponse>retrievePatientEncountersAndObservation(@RequestParam(name = "family") String family,@RequestParam(name = "given") String givenName, @RequestParam(name = "identifier") Long identifier,@RequestParam(name = "birthDate") String birthDate, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size){
        return ResponseEntity.ok(patientService.retrievePatientEncountersAndObservation(family, givenName,identifier,birthDate,page,size));
    }
    @Operation(summary = "view Patient encounters")
    @GetMapping("/{id}/encounters")
    public ResponseEntity<UniversalResponse>viewPatientEncounters(@PathVariable("id")Long patientId){
        return ResponseEntity.ok(patientService.viewPatientEncounters(patientId));
    }
    @GetMapping("/{id}/observations")
    @Operation(summary = "view Patient Observations")
    public ResponseEntity<UniversalResponse>viewPatientObservations(@PathVariable("id")Long patientId){
        return ResponseEntity.ok(patientService.viewPatientObservations(patientId));
    }
}
