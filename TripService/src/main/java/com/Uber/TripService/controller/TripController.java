package com.Uber.TripService.controller;


import com.Uber.TripService.dto.DriverRespondDto;
import com.Uber.TripService.dto.TripDto;
import com.Uber.TripService.dto.TripRequestDto;
import com.Uber.TripService.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")

public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // NOTE: In production get riderId from JWT; for demo we accept an explicit riderId in body or header
    @PostMapping("/request")
    public ResponseEntity<TripDto> requestTrip(@Validated @RequestBody TripRequestDto req, @RequestHeader(value="X-USER-ID", required=false) Long userId) {
        Long riderId = (req.getRiderId() != null) ? req.getRiderId() : userId;
        if (riderId == null) throw new IllegalArgumentException("riderId missing");
        TripDto dto = tripService.requestTrip(riderId, req);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{tripUuid}")
    public ResponseEntity<TripDto> getTrip(@PathVariable String tripUuid) {
        TripDto dto = tripService.getTripByUuid(UUID.fromString(tripUuid));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{tripUuid}/driver/respond")
    public ResponseEntity<Void> driverRespond(@PathVariable String tripUuid, @RequestBody DriverRespondDto r) {
        tripService.handleDriverResponse(UUID.fromString(tripUuid), r.getDriverId(), r.getDecision());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripUuid}/start")
    public ResponseEntity<Void> start(@PathVariable String tripUuid, @RequestHeader("X-USER-ID") Long driverId){
        tripService.startTrip(UUID.fromString(tripUuid), driverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripUuid}/complete")
    public ResponseEntity<Void> complete(@PathVariable String tripUuid, @RequestHeader("X-USER-ID") Long driverId){
        tripService.completeTrip(UUID.fromString(tripUuid), driverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripUuid}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String tripUuid, @RequestParam(required=false) String reason) {
        tripService.cancelTrip(UUID.fromString(tripUuid), reason);
        return ResponseEntity.ok().build();
    }
}
