package com.Uber.TripService;


//import com.example.tripservice.dto.*;
//import com.example.tripservice.entity.*;

import com.Uber.TripService.dto.TripDto;
import com.Uber.TripService.model.Trip;

public class Mapper {
    public static TripDto toDto(Trip t){
        if (t == null) return null;
        return TripDto.builder()
                .tripUuid(t.getTripUuid())
                .riderId(t.getRiderId())
                .driverId(t.getDriverId())
                .status(t.getStatus())
                .origin(LocationDto.builder().lat(t.getOriginLat()).lng(t.getOriginLng()).build())
                .destination(t.getDestLat() != null ? LocationDto.builder().lat(t.getDestLat()).lng(t.getDestLng()).build() : null)
                .estimatedFare(t.getEstimatedFare())
                .fareAmount(t.getFareAmount())
                .requestedAt(t.getRequestedAt())
                .build();
    }
}
