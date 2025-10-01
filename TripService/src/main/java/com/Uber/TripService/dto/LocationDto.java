package com.Uber.TripService.dto;


import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LocationDto {
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;
}
