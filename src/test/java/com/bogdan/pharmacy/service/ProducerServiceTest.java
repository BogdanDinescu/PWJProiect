package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.ProducerMapper;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.repository.ProducerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @InjectMocks
    private ProducerService service;
    @Mock
    private ProducerRepository repository;
    @Mock
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
    void getProducer() {
        when(repository.findById(existingProducerDto.getId())).thenReturn(Optional.ofNullable(existingProducer));
        when(repository.findById(nonExistingProducerDto.getId())).thenThrow(new NotFoundException("producer"));
        assertThrows(NotFoundException.class, () -> {service.getProducer(nonExistingProducerDto.getId());});
        assertEquals(service.getProducer(existingProducerDto.getId()).getId(), existingProducer.getId());
        assertEquals(service.getProducer(existingProducerDto.getId()).getName(), existingProducer.getName());
    }

    @Test
    void addProducer() {
        when(mapper.dtoToProducer(existingProducerDto)).thenReturn(existingProducer);
        when(repository.save(existingProducer)).thenReturn(existingProducer);
        service.addProducer(existingProducerDto);
    }

    @Test
    void updateProducer() {
        when(repository.existsById(existingProducer.getId())).thenReturn(true);
        when(repository.existsById(nonExistingProducer.getId())).thenReturn(false);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToProducer(existingProducerDto)).thenReturn(existingProducer);
        when(mapper.dtoToProducer(nonExistingProducerDto)).thenReturn(nonExistingProducer);
        assertTrue(service.updateProducer(existingProducerDto.getId(), existingProducerDto));
        assertFalse(service.updateProducer(nonExistingProducerDto.getId(), nonExistingProducerDto));
        assertFalse(service.updateProducer(4L, existingProducerDto));
    }

    @Test
    void removeProducer() {
        when(repository.findById(existingProducerDto.getId())).thenReturn(Optional.ofNullable(existingProducer));
        when(repository.findById(nonExistingProducerDto.getId())).thenThrow(new NotFoundException("city"));
        assertThrows(NotFoundException.class, () -> {service.removeProducer(nonExistingProducerDto.getId());});
        service.removeProducer(existingProducerDto.getId());
    }
}