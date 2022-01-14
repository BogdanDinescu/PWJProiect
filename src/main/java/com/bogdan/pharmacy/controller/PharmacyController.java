package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.dao.PharmacyDto;
import com.bogdan.pharmacy.dao.PharmacyUI;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.service.PharmacyService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/pharmacy")
public class PharmacyController {
    private final PharmacyService service;
    private final PharmacyMapper mapper;
    private final DrugMapper drugMapper;

    public PharmacyController(@Autowired PharmacyService service, @Autowired PharmacyMapper mapper, @Autowired DrugMapper drugMapper) {
        this.service = service;
        this.mapper = mapper;
        this.drugMapper = drugMapper;
    }

    @Operation(summary = "Get pharmacy by id", operationId = "getPharmacyById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found pharmacy",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PharmacyDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found pharmacy")

    })
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDto> getPharmacyById(@PathVariable Long id) {
        Pharmacy pharmacy = service.getPharmacy(id);
        return ResponseEntity.ok(mapper.pharmacyToDto(pharmacy));
    }

    @Operation(summary = "Get all pharmacies", operationId = "getAllPharmacies")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found pharmacies",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),

    })
    @GetMapping
    public ResponseEntity<List<PharmacyUI>> getAllPharmacies() {
        List<Pharmacy> pharmacies = service.getPharmacies();
        return ResponseEntity.ok(mapper.pharmacyListToUIList(pharmacies));
    }

    @Operation(summary = "Get pharmacy inventory", operationId = "getPharmacyDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found list",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Not found pharmacy")

    })
    @GetMapping("/{id}/drugs")
    public ResponseEntity<List<DrugDto>> getPharmacyDrugs(@PathVariable Long id) {
        Pharmacy pharmacy = service.getPharmacy(id);
        return ResponseEntity.ok(drugMapper.drugListToDtoList(pharmacy.getDrugs()));
    }

    @Operation(summary = "Create a pharmacy", operationId = "addPharmacy")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pharmacy was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found city id"),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addPharmacy(@RequestBody @Valid PharmacyDto pharmacyDto) {
        Pharmacy pharmacy =  service.addPharmacy(pharmacyDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(pharmacy.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Add drugs to pharmacy", operationId = "addDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drugs removed"),
            @ApiResponse(responseCode = "404", description = "Pharmacy not found")
    })
    @PostMapping("/{pharmacy_id}/drugs/")
    public ResponseEntity<Void> addDrugs(@PathVariable Long pharmacy_id, @RequestBody List<Long> ids) {
        service.addDrugs(pharmacy_id, ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add drug to pharmacy", operationId = "addDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug added"),
            @ApiResponse(responseCode = "404", description = "Drug or pharmacy not found")
    })
    @PostMapping("/{pharmacy_id}/drugs/{drug_id}")
    public ResponseEntity<Void> addDrug(@PathVariable Long pharmacy_id, @PathVariable Long drug_id) {
        service.addDrug(pharmacy_id, drug_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a pharmacy", operationId = "updatePharmacy")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pharmacy was updated"),
            @ApiResponse(responseCode = "201", description = "Pharmacy was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found city id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePharmacy(@PathVariable Long id, @RequestBody @Valid PharmacyDto pharmacy) {
        if (service.updatePharmacy(id, pharmacy)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Delete a pharmacy", operationId = "removePharmacy")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pharmacy was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Pharmacy not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePharmacy(@PathVariable Long id) {
        service.removePharmacy(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove drug from pharmacy", operationId = "removeDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug removed"),
            @ApiResponse(responseCode = "404", description = "Drug or pharmacy not found")
    })
    @DeleteMapping("{pharmacy_id}/drugs/{drug_id}")
    public ResponseEntity<Void> removeDrug(@PathVariable Long pharmacy_id, @PathVariable Long drug_id) {
        service.removeDrug(pharmacy_id, drug_id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove drugs from pharmacy", operationId = "removeDrugs")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drugs removed"),
            @ApiResponse(responseCode = "404", description = "Pharmacy not found")
    })
    @DeleteMapping("/{pharmacy_id}/drugs/")
    public ResponseEntity<Void> removeDrugs(@PathVariable Long pharmacy_id, @RequestBody List<Long> ids) {
        service.removeDrugs(pharmacy_id, ids);
        return ResponseEntity.noContent().build();
    }
}
