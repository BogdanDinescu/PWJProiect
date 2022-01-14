package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.ProducerMapper;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.service.ProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProducerController.class)
class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProducerService service;
    @MockBean
    private ProducerMapper mapper;

    private static ProducerDto existingProducerDto;
    private static Producer existingProducer;
    private static ProducerDto nonExistingProducerDto;
    private static Producer nonExistingProducer;

    @BeforeEach
    void setUp() {
        existingProducerDto = new ProducerDto(1L, "Prod");
        existingProducer = new Producer(1L, "Prod");
        nonExistingProducer = new Producer(2L, "Prod2");
        nonExistingProducerDto = new ProducerDto(2L, "Prod2");
    }

    @Test
    void getProducerById() throws Exception {
        String endpoint = "/api/producer/%d";
        when(service.getProducer(existingProducer.getId())).thenReturn(existingProducer);
        when(service.getProducer(nonExistingProducer.getId())).thenThrow(new NotFoundException("producer"));
        mockMvc.perform(get(String.format(endpoint, existingProducerDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistingProducerDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addProducer() throws Exception{
        String endpoint = "/api/producer";
        when(service.addProducer(existingProducerDto)).thenReturn(existingProducer);
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingProducerDto))).andExpect(status().isCreated());
    }

    @Test
    void updateProducer() throws Exception {
        String endpoint = "/api/producer/%d";
        when(service.updateProducer(existingProducer.getId(), existingProducerDto)).thenReturn(true);
        when(service.updateProducer(nonExistingProducerDto.getId(), nonExistingProducerDto)).thenReturn(false);
        mockMvc.perform(put(String.format(endpoint, existingProducerDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingProducerDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistingProducerDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistingProducerDto))).andExpect(status().isCreated());
    }

    @Test
    void removeProducer() throws Exception {
        String endpoint = "/api/producer/%d";
        doThrow(new NotFoundException("city")).when(service).removeProducer(nonExistingProducerDto.getId());
        doNothing().when(service).removeProducer(existingProducerDto.getId());
        mockMvc.perform(delete(String.format(endpoint, existingProducerDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistingProducerDto.getId()))).andExpect(status().isNotFound());
    }
}