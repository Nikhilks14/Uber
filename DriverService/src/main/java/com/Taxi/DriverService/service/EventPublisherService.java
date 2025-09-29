//package com.Taxi.DriverService.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class EventPublisherService {
//    private final KafkaTemplate<String, Object> kafka;
//    public void publishDriverOnboardRequested(Long driverId, Long userId) {
//        Map<String,Object> e = Map.of("eventType","DriverOnboardRequested","driverId",driverId,"userId",userId);
//        kafka.send("driver-events", driverId.toString(), e);
//    }
//    public void publishDriverAvailabilityChanged(Long driverId, Availability availability) {
//        kafka.send("driver-events", driverId.toString(), Map.of("eventType","DriverAvailabilityChanged","driverId",driverId,"availability",availability));
//    }
//    public void publishDriverVerified(Long driverId, boolean approved, Long adminId) {
//        kafka.send("driver-events", driverId.toString(), Map.of("eventType","DriverVerified","driverId",driverId,"approved",approved,"adminId",adminId));
//    }
//}
