package com.Uber.TripService.controller;

//import com.example.tripservice.dto.LocationDto;
//import com.example.tripservice.service.RedisGeoService;
//import com.example.tripservice.service.KafkaProducerService;
import com.Uber.TripService.dto.LocationDto;
import com.Uber.TripService.service.KafkaProducerService;
import com.Uber.TripService.service.RedisGeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {
    private final RedisGeoService geoService;
    private final KafkaProducerService kafkaProducer;

    public LocationController(RedisGeoService geoService, KafkaProducerService kafkaProducer) {
        this.geoService = geoService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/drivers/{driverId}")
    public ResponseEntity<Void> update(@PathVariable Long driverId, @RequestBody LocationDto dto) {
        geoService.updateDriverLocation(driverId, dto.getLat(), dto.getLng());
        // publish lightweight event for downstream analytics / UI push
        kafkaProducer.publish("location-updates", Map.of(
                "driverId", driverId, "lat", dto.getLat(), "lng", dto.getLng(), "ts", Instant.now().toString()
        ));
        return ResponseEntity.ok().build();
    }
}
