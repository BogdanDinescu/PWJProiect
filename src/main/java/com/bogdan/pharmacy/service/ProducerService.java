package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.ProducerMapper;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.repository.ProducerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProducerService {
    private final ProducerRepository repository;
    private final ProducerMapper mapper;

    public ProducerService(@Autowired ProducerRepository repository, @Autowired ProducerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Producer getProducer(Long id) {
        Optional<Producer> producer = repository.findById(id);
        if (producer.isEmpty()) {
            throw new NotFoundException("producer");
        }
        return producer.get();
    }

    public Producer addProducer(ProducerDto producer) {
        producer.setId(null);
        return repository.save(mapper.dtoToProducer(producer));
    }

    public boolean updateProducer(Long id, ProducerDto producerDto) {
        boolean exists = repository.existsById(id);
        Producer producer = mapper.dtoToProducer(producerDto);
        producer.setId(id);
        repository.save(producer);
        return exists;
    }

    public void removeProducer(Long id) {
        Producer producer = getProducer(id);
        repository.delete(producer);
    }
}
