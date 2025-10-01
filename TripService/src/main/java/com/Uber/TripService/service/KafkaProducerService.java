package com.Uber.TripService.service;

package com.example.tripservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object payload) {
        kafkaTemplate.send(topic, payload);
    }
}
