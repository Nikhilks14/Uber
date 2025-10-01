package com.Uber.TripService.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DriverRespondDto {
    private Long driverId;
    private String decision; // ACCEPT | DECLINE
}
