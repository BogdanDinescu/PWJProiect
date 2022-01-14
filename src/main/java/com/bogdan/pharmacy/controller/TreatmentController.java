package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.service.TreatmentService;
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
import java.util.List;

@Controller
@RequestMapping("api/treatment")
public class TreatmentController {
    private final TreatmentService service;
    private final TreatmentMapper mapper;
    private final DrugMapper drugMapper;

    public TreatmentController(@Autowired TreatmentService service, @Autowired TreatmentMapper mapper, @Autowired DrugMapper drugMapper) {
        this.service = service;
        this.mapper = mapper;
        this.drugMapper = drugMapper;
    }

    @Operation(summary = "Get treatment by id", operationId = "getTreatmentById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found treatment",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TreatmentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found treatment")

    })
    @GetMapping("/{id}")
    public ResponseEntity<TreatmentDto> getTreatmentById(@PathVariable Long id) {
        Treatment treatment = service.getTreatment(id);
        return ResponseEntity.ok(mapper.treatmentToDto(treatment));
    }

    @Operation(summary = "Get treatment drug list", operationId = "getTreatmentDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found list",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Not found treatment")

    })
    @GetMapping("/{id}/drugs")
    public ResponseEntity<List<DrugDto>> getTreatmentDrugs(@PathVariable Long id) {
        Treatment treatment = service.getTreatment(id);
        return ResponseEntity.ok(drugMapper.drugListToDtoList(treatment.getDrugs()));
    }

    @Operation(summary = "Create a treatment", operationId = "addTreatment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Treatment was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addTreatment(@RequestBody @Valid TreatmentDto treatmentDto) {
        Treatment treatment = service.addTreatment(treatmentDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(treatment.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update a treatment", operationId = "updateTreatment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatment was updated"),
            @ApiResponse(responseCode = "201", description = "Treatment was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTreatment(@PathVariable Long id, @RequestBody @Valid TreatmentDto treatment) {
        if (service.updateTreatment(id, treatment)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Add drugs to treatment", operationId = "addDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drugs added"),
            @ApiResponse(responseCode = "404", description = "Pharmacy not found")
    })
    @PostMapping("/{treatment_id}/drugs/")
    public ResponseEntity<Void> addDrugs(@PathVariable Long treatment_id, @RequestBody List<Long> ids) {
        service.addDrugs(treatment_id, ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add drug to treatment", operationId = "addDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug added"),
            @ApiResponse(responseCode = "404", description = "Drug or treatment not found")
    })
    @PostMapping("/{treatment_id}/drugs/{drug_id}")
    public ResponseEntity<Void> addDrug(@PathVariable Long treatment_id, @PathVariable Long drug_id) {
        service.addDrug(treatment_id, drug_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a treatment", operationId = "removeTreatment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatment was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Treatment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeTreatment(@PathVariable Long id) {
        service.removeTreatment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove drugs from treatment", operationId = "removeDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drugs removed"),
            @ApiResponse(responseCode = "404", description = "Treatment not found")
    })
    @DeleteMapping("/{treatment_id}/drugs/")
    public ResponseEntity<Void> removeDrugs(@PathVariable Long treatment_id, @RequestBody List<Long> ids) {
        service.removeDrugs(treatment_id, ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove drug from treatment", operationId = "removeDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug removed"),
            @ApiResponse(responseCode = "404", description = "Drug or treatment not found")
    })
    @DeleteMapping("/{treatment_id}/drugs/{drug_id}")
    public ResponseEntity<Void> removeDrug(@PathVariable Long treatment_id, @PathVariable Long drug_id) {
        service.removeDrug(treatment_id, drug_id);
        return ResponseEntity.noContent().build();
    }
}
