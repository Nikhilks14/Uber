package com.Uber.TripService.repo;



import com.Uber.TripService.model.TripEventAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripEventAuditRepository extends JpaRepository<TripEventAudit, Long> {
}
