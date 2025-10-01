package com.Uber.TripService.dto;


import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequestDto {
    @NotNull
    private LocationDto origin;
    private LocationDto destination;
    private Long riderId; // optional if taken from JWT/gateway
    private String paymentMethodId;
}
