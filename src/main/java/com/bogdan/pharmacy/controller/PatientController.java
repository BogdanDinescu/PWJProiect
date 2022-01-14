package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.PatientDto;
import com.bogdan.pharmacy.dao.PharmacyUI;
import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.mapper.PatientMapper;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Patient;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("api/patient")
public class PatientController {
    private final PatientService service;
    private final PatientMapper mapper;
    private final TreatmentMapper treatmentMapper;
    private final PharmacyMapper pharmacyMapper;

    public PatientController(@Autowired PatientService service, @Autowired PatientMapper mapper, @Autowired TreatmentMapper treatmentMapper, @Autowired PharmacyMapper pharmacyMapper) {
        this.service = service;
        this.mapper = mapper;
        this.treatmentMapper = treatmentMapper;
        this.pharmacyMapper = pharmacyMapper;
    }

    @Operation(summary = "Get patient by id", operationId = "getPatientById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found patient",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found patient")

    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        Patient patient = service.getPatient(id);
        return ResponseEntity.ok(mapper.patientToDto(patient));
    }

    @Operation(summary = "Get patient treatments", operationId = "getPatientTreatments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found list",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Not found patient")

    })
    @GetMapping("/{id}/treatments")
    public ResponseEntity<List<TreatmentDto>> getPatientTreatments(@PathVariable Long id) {
        Patient patient = service.getPatient(id);
        return ResponseEntity.ok(treatmentMapper.treatmentListToDtoList(patient.getTreatments()));
    }


    @Operation(summary = "Get pharmacies where patient with give id can find the drugs for all treatments", operationId = "getPharmacies")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found list",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Not found patient")

    })
    @GetMapping("/{id}/pharmacies")
    public ResponseEntity<List<PharmacyUI>> getPharmacies(@PathVariable Long id) {
        Patient patient = service.getPatient(id);
        List<Treatment> treatments = patient.getTreatments();
        List<Drug> drugs = new ArrayList<>();
        Set<Pharmacy> pharmacies = new HashSet<>();
        treatments.forEach(treatment -> {
            drugs.addAll(treatment.getDrugs());
        });
        drugs.forEach(drug -> {
            pharmacies.addAll(drug.getPharmacy());
        });
        return ResponseEntity.ok(pharmacyMapper.pharmacyListToUIList(new ArrayList<>(pharmacies)));
    }

    @Operation(summary = "Create a patient", operationId = "addPatient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found city id"),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addPatient(@RequestBody @Valid PatientDto patientDto) {
        Patient patient = service.addPatient(patientDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(patient.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update a patient", operationId = "updatePatient")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Patient was updated"),
            @ApiResponse(responseCode = "201", description = "Patient was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found city id"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(@PathVariable Long id, @RequestBody @Valid PatientDto patient) {
        if (service.updatePatient(id, patient)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }


    @Operation(summary = "Add treatment to patient", operationId = "addTreatment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatment added"),
            @ApiResponse(responseCode = "404", description = "Patient or treatment not found")
    })
    @PostMapping("/{patient_id}/treatments/{treatment_id}")
    public ResponseEntity<Void> addTreatment(@PathVariable Long patient_id, @PathVariable Long treatment_id) {
        service.addTreatment(patient_id, treatment_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add treatments to patient", operationId = "addTreatments")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatments added"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PostMapping("{patient_id}/treatments/")
    public ResponseEntity<Void> addTreatments(@PathVariable Long patient_id, @RequestBody List<Long> ids) {
        service.addTreatments(patient_id, ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a patient", operationId = "removePatient")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Patient was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePatient(@PathVariable Long id) {
        service.removePatient(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Remove treatment to patient", operationId = "removeTreatment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatment removed"),
            @ApiResponse(responseCode = "404", description = "Patient or treatment not found")
    })
    @DeleteMapping("/{patient_id}/treatments/{treatment_id}")
    public ResponseEntity<Void> removeTreatment(@PathVariable Long patient_id, @PathVariable Long treatment_id) {
        service.removeTreatment(patient_id, treatment_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove treatment to patient", operationId = "removeTreatments")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatments removed"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("{patient_id}/treatments/")
    public ResponseEntity<Void> removeTreatments(@PathVariable Long patient_id, @RequestBody List<Long> ids) {
        service.removeTreatments(patient_id, ids);
        return ResponseEntity.noContent().build();
    }
}
