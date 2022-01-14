package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.service.DrugService;
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

@RestController
@RequestMapping("api/drug")
public class DrugController {
    private final DrugService service;
    private final DrugMapper mapper;

    public DrugController(@Autowired DrugService service, @Autowired DrugMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Get drug by id", operationId = "getDrugById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found drug",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DrugDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found drug")

    })
    @GetMapping("/{id}")
    public ResponseEntity<DrugDto> getDrugById(@PathVariable Long id) {
        Drug drug = service.getDrug(id);
        return ResponseEntity.ok(mapper.drugToDto(drug));
    }

    @Operation(summary = "Create a drug", operationId = "addDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Drug was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found producer id"),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addDrug(@RequestBody @Valid DrugDto drugDto) {
        Drug drug = service.addDrug(drugDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(drug.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update a drug", operationId = "updateDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug was updated"),
            @ApiResponse(responseCode = "201", description = "Drug was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "404", description = "Not found producer id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDrug(@PathVariable Long id, @RequestBody @Valid DrugDto drug) {
        if (service.updateDrug(id, drug)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Delete a Drug", operationId = "removeDrug")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Drug was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Drug not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDrug(@PathVariable Long id) {
        service.removeDrug(id);
        return ResponseEntity.noContent().build();
    }
}
