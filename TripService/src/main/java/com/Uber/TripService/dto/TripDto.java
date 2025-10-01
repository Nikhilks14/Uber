package com.Uber.TripService.dto;


import com.Uber.TripService.model.TripStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripDto {
    private UUID tripUuid;
    private Long riderId;
    private Long driverId;
    private TripStatus status;
    private LocationDto origin;
    private LocationDto destination;
    private BigDecimal estimatedFare;
    private BigDecimal fareAmount;
    private Instant requestedAt;
}
