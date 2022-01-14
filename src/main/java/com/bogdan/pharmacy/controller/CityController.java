package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.CityDto;
import com.bogdan.pharmacy.mapper.CityMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.service.CityService;
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
@RequestMapping("api/city")
public class CityController {
    private final CityService service;
    private final CityMapper mapper;

    public CityController(@Autowired CityService service, @Autowired CityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Get city by id", operationId = "getCityById")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found city",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CityDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found city")

    })
    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        City city = service.getCity(id);
        return ResponseEntity.ok(mapper.cityToDto(city));
    }

    @Operation(summary = "Create a city", operationId = "addCity")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "City was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid json")
    })
    @PostMapping
    public ResponseEntity<Void> addCity(@RequestBody CityDto cityDto) {
        City city = service.addCity(cityDto);
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(city.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update a city", operationId = "updateCity")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City was updated"),
            @ApiResponse(responseCode = "201", description = "City was created",
                    headers={@Header(name ="location", schema = @Schema(type = "String"))}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCity(@PathVariable Long id, @RequestBody @Valid CityDto city) {
        if (service.updateCity(id, city)) {
            return ResponseEntity.noContent().build();
        }
        URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Delete a City", operationId = "removeCity")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCity(@PathVariable Long id) {
        service.removeCity(id);
        return ResponseEntity.noContent().build();
    }
}
