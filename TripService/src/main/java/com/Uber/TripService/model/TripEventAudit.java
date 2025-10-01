package com.Uber.TripService.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "trip_events_audit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripEventAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "payload", columnDefinition = "text")
    private String payload;

    @Column(name = "created_at")
    private Instant createdAt;
}
