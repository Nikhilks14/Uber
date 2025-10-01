package com.Uber.TripService.service;


import com.Uber.TripService.Mapper;
import com.Uber.TripService.dto.LocationDto;
import com.Uber.TripService.dto.TripDto;
import com.Uber.TripService.dto.TripRequestDto;
import com.Uber.TripService.model.Trip;
import com.Uber.TripService.model.TripEventAudit;
import com.Uber.TripService.model.TripStatus;
import com.Uber.TripService.repo.TripEventAuditRepository;
import com.Uber.TripService.repo.TripOfferRepository;
import com.Uber.TripService.repo.TripRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final TripOfferRepository tripOfferRepository;
    private final TripEventAuditRepository auditRepository;
    private final KafkaProducerService kafkaProducer;
    private final MatchingService matchingService;

    public TripService(TripRepository tripRepository, TripOfferRepository tripOfferRepository, TripEventAuditRepository auditRepository, KafkaProducerService kafkaProducer, MatchingService matchingService) {
        this.tripRepository = tripRepository;
        this.tripOfferRepository = tripOfferRepository;
        this.auditRepository = auditRepository;
        this.kafkaProducer = kafkaProducer;
        this.matchingService = matchingService;
    }

    @Transactional
    public TripDto requestTrip(Long riderId, TripRequestDto req) {
        Trip trip = Trip.builder()
                .tripUuid(UUID.randomUUID())
                .riderId(riderId)
                .originLat(req.getOrigin().getLat())
                .originLng(req.getOrigin().getLng())
                .destLat(req.getDestination() != null ? req.getDestination().getLat() : null)
                .destLng(req.getDestination() != null ? req.getDestination().getLng() : null)
                .status(TripStatus.REQUESTED)
                .requestedAt(Instant.now())
                .estimatedFare(estimateFare(req.getOrigin(), req.getDestination()))
                .build();

        trip = tripRepository.save(trip);

        // publish TripRequested event
        kafkaProducer.publish("trip-events", Map.of(
                "eventType", "TripRequested",
                "tripId", trip.getTripUuid().toString(),
                "riderId", riderId,
                "origin", Map.of("lat", trip.getOriginLat(), "lng", trip.getOriginLng()),
                "requestedAt", trip.getRequestedAt().toString()
        ));

        // audit
        auditRepository.save(TripEventAudit.builder()
                .tripId(trip.getId())
                .eventType("TripRequested")
                .payload("origin:" + trip.getOriginLat() + "," + trip.getOriginLng())
                .createdAt(Instant.now())
                .build());

        // run matching (synchronously for this skeleton)
        matchingService.handleTripRequest(trip);

        return Mapper.toDto(trip);
    }

    private BigDecimal estimateFare(LocationDto origin, LocationDto destination) {
        // naive Haversine cost approximation — replace with pricing service call
        if (origin == null || destination == null) return BigDecimal.valueOf(50.0);
        double dKm = haversineKm(origin.getLat(), origin.getLng(), destination.getLat(), destination.getLng());
        double base = 30.0;
        double perKm = 12.0;
        return BigDecimal.valueOf(base + perKm * dKm);
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    @Transactional
    public void handleDriverResponse(UUID tripUuid, Long driverId, String decision) {
        Trip trip = tripRepository.findByTripUuid(tripUuid)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        if ("ACCEPT".equalsIgnoreCase(decision)) {
            // optimistic locking will prevent double assignment in concurrent scenarios
            if (trip.getDriverId() != null) {
                // already assigned
                return;
            }
            trip.setDriverId(driverId);
            trip.setStatus(TripStatus.ACCEPTED);
            trip.setAssignedAt(Instant.now());
            trip.setAcceptedAt(Instant.now());
            tripRepository.save(trip);

            kafkaProducer.publish("trip-events", Map.of(
                    "eventType", "DriverAssigned",
                    "tripId", trip.getTripUuid().toString(),
                    "driverId", driverId
            ));

            auditRepository.save(TripEventAudit.builder()
                    .tripId(trip.getId())
                    .eventType("DriverAccepted")
                    .payload("driverId:" + driverId)
                    .createdAt(Instant.now()).build());
        } else {
            // decline
            auditRepository.save(TripEventAudit.builder()
                    .tripId(trip.getId())
                    .eventType("DriverDeclined")
                    .payload("driverId:" + driverId + " declined")
                    .createdAt(Instant.now()).build());
        }
    }

    @Transactional
    public void startTrip(UUID tripUuid, Long driverId) {
        Trip trip = tripRepository.findByTripUuid(tripUuid).orElseThrow();
        if (!driverId.equals(trip.getDriverId())) throw new IllegalArgumentException("driver mismatch");
        trip.setStatus(TripStatus.ONGOING);
        trip.setStartedAt(Instant.now());
        tripRepository.save(trip);
        kafkaProducer.publish("trip-events", Map.of("eventType","TripStarted","tripId",trip.getTripUuid().toString()));
    }

    @Transactional
    public void completeTrip(UUID tripUuid, Long driverId) {
        Trip trip = tripRepository.findByTripUuid(tripUuid).orElseThrow();
        if (!driverId.equals(trip.getDriverId())) throw new IllegalArgumentException("driver mismatch");
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(Instant.now());
        // finalize fare (placeholder)
        if (trip.getFareAmount() == null) {
            trip.setFareAmount(trip.getEstimatedFare());
        }
        tripRepository.save(trip);
        kafkaProducer.publish("trip-events", Map.of("eventType","TripCompleted","tripId",trip.getTripUuid().toString(),"fareAmount",trip.getFareAmount()));
    }

    public TripDto getTripByUuid(UUID tripUuid) {
        return tripRepository.findByTripUuid(tripUuid)
                .map(Mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    }

    @Transactional
    public void cancelTrip(UUID tripUuid, String reason) {
        Trip trip = tripRepository.findByTripUuid(tripUuid).orElseThrow();
        trip.setStatus(TripStatus.CANCELLED);
        trip.setCancelledAt(Instant.now());
        tripRepository.save(trip);
        kafkaProducer.publish("trip-events", Map.of("eventType","TripCancelled","tripId",trip.getTripUuid().toString(), "reason", reason));
    }
}
