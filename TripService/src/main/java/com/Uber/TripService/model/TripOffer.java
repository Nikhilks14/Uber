package com.Uber.TripService.model;

import com.Uber.TripService.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "trip_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripOffer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "driver_id")
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;

    @Column(name = "offered_at")
    private Instant offeredAt;

    @Column(name = "responded_at")
    private Instant respondedAt;
}