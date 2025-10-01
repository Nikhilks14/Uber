package com.Uber.TripService.repo;


import com.Uber.TripService.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByTripUuid(UUID uuid);
}
