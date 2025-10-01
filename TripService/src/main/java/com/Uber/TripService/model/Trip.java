package com.Uber.TripService.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_uuid", unique = true, updatable = false, nullable = false)
    private UUID tripUuid = UUID.randomUUID();

    @PrePersist
    public void generateUuid(){
        if(tripUuid == null) tripUuid = UUID.fromString(UUID.randomUUID().toString());
    }



    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "origin_lat")
    private Double originLat;
    @Column(name = "origin_lng")
    private Double originLng;

    @Column(name = "dest_lat")
    private Double destLat;
    @Column(name = "dest_lng")
    private Double destLng;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Column(name = "requested_at")
    private Instant requestedAt;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "estimated_fare")
    private BigDecimal estimatedFare;

    @Column(name = "fare_amount")
    private BigDecimal fareAmount;

    @Column(name = "surge_multiplier")
    private Double surgeMultiplier = 1.0;

    @Version
    private Long version;
}