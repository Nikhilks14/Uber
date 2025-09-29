package com.Taxi.DriverService.repo;

import com.Taxi.DriverService.enums.DriverStatus;
import com.Taxi.DriverService.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepo extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUserId(Long userId);
    List<Driver> findByStatus(DriverStatus status);
}
