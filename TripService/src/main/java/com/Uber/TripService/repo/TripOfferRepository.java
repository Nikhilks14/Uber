package com.Uber.TripService.repo;

import com.Uber.TripService.model.TripOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripOfferRepository extends JpaRepository<TripOffer, Long> {
}
