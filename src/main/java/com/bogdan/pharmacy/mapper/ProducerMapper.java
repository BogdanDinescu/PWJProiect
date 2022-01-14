package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.ProducerDto;
import com.bogdan.pharmacy.model.Producer;
import org.springframework.stereotype.Component;

@Component
public class ProducerMapper {
    public ProducerDto producerToDto(Producer producer) {
        return new ProducerDto(producer.getId(), producer.getName());
    }

    public Producer dtoToProducer(ProducerDto producer) {
        return new Producer(producer.getId(), producer.getName());
    }
}
