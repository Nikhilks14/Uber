//package com.Taxi.DriverService.service;
//
//import com.Taxi.DriverService.model.Driver;
//import com.Taxi.DriverService.repo.DriverDocumentRepository;
//import com.Taxi.DriverService.repo.DriverRepo;
//import com.Taxi.DriverService.repo.VehicleRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class DriverService {
//    private final DriverRepo driverRepo;
//    private final VehicleRepository vehicleRepo;
//    private final DriverDocumentRepository docRepo;
//    private final EventPublisherService events;
//
//    @Transactional
//    public Driver createOnboard(Long userId, DriverDto dto) {
//        if (driverRepo.findByUserId(userId).isPresent()) {
//            throw new IllegalStateException("Driver already exists");
//        }
//        Driver d = new Driver();
//        d.setUserId(userId);
//        d.setFullName(dto.getFullName());
//        d.setPhone(dto.getPhone());
//        d.setEmail(dto.getEmail());
//        d.setStatus(DriverStatus.PENDING);
//        driverRepo.save(d);
//        // create vehicle
//        Vehicle v = new Vehicle();
//        v.setDriver(d);
//        v.setVehicleNumber(dto.getVehicleNumber());
//        v.setModel(dto.getVehicleModel());
//        v.setCapacity(dto.getVehicleCapacity());
//        vehicleRepo.save(v);
//
//        // publish event for downstream (e.g., admin dashboard)
//        events.publishDriverOnboardRequested(d.getId(), userId);
//
//        return d;
//    }
//
//    public void setAvailability(Long driverId, Availability availability) {
//        Driver d = driverRepo.findById(driverId).orElseThrow();
//        d.setAvailability(availability);
//        driverRepo.save(d);
//        events.publishDriverAvailabilityChanged(driverId, availability);
//    }
//
//    public Driver verifyDriver(Long driverId, boolean accept, String comment, Long adminId) {
//        Driver d = driverRepo.findById(driverId).orElseThrow();
//        d.setStatus(accept ? DriverStatus.ACTIVE : DriverStatus.SUSPENDED);
//        driverRepo.save(d);
//        events.publishDriverVerified(driverId, accept, adminId);
//        return d;
//    }
//}
//
