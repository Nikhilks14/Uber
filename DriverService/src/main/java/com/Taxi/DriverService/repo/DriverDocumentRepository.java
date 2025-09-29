package com.Taxi.DriverService.repo;

import com.Taxi.DriverService.model.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {
    List<DriverDocument> findByDriverId(Long driverId);
}
