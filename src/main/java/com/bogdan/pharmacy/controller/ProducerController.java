package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.mapper.ProducerMapper;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.service.ProducerService;
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
@RequestMapping("api/producer")
public class ProducerController {

    private final ProducerService service;
    private final ProducerMapper mapper;

    public ProducerController(@Autowired ProducerService service, @Autowired ProducerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Get producer by id", operationId = "getProducerById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found producer",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProducerDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found producer")

    })
    @GetMapping("/{id}")
    public ResponseEntity<ProducerDto> getProducerById(@PathVariable Long id) {
        Producer producer = service.getProducer(id);
        return ResponseEntity.ok(mapper.producerToDto(producer));
    }

    @Operation(summary = "Create a producer", operationId = "addProducer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producer was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addProducer(@RequestBody @Valid ProducerDto producerDto) {
        Producer producer = service.addProducer(producerDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(producer.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update a producer", operationId = "updateProducer")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producer was updated"),
            @ApiResponse(responseCode = "201", description = "Producer was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProducer(@PathVariable Long id, @RequestBody @Valid ProducerDto producerDto) {
        if (service.updateProducer(id, producerDto)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Delete a producer", operationId = "removeProducer")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producer was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Producer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeProducer(@PathVariable Long id) {
        service.removeProducer(id);
        return ResponseEntity.noContent().build();
    }
}
