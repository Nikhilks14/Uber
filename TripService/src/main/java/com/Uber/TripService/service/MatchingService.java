package com.Uber.TripService.service;

//import com.example.tripservice.entity.Trip;
//import com.example.tripservice.repository.TripRepository;
import com.Uber.TripService.model.Trip;
import com.Uber.TripService.repo.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final RedisGeoService geoService;
    private final KafkaProducerService kafkaProducer;
    private final TripRepository tripRepository;

    private static final String OFFER_TPL = "trip:%s:offer:%s"; // tripUuid, driverId
    private static final int OFFER_TTL_SECONDS = 12;

    /**
     * Simple matching: query nearby drivers (3km) and publish matching-commands offers.
     * This method is synchronous in this sample; in production you'd run it on matching workers consuming TripRequested events.
     */
    public void handleTripRequest(Trip trip) {
        double lat = trip.getOriginLat();
        double lng = trip.getOriginLng();
        List<Long> candidates = geoService.findNearby(lat, lng, 3.0, 20);
        if (candidates.isEmpty()) {
            // publish a "no-drivers-found" event if needed
            kafkaProducer.publish("trip-events", Map.of(
                    "eventType", "NoDriversFound",
                    "tripId", trip.getTripUuid().toString()
            ));
            return;
        }

        // offer in parallel to top-K (parallel strategy)
        int K = Math.min(3, candidates.size());
        for (int i = 0; i < K; i++) {
            Long driverId = candidates.get(i);
            String offerKey = String.format(OFFER_TPL, trip.getTripUuid().toString(), driverId);
            boolean offered = geoService.setOfferIfAbsent(offerKey, OFFER_TTL_SECONDS);
            if (offered) {
                // publish matching command — driver app or driver-service would consume
                kafkaProducer.publish("matching-commands", Map.of(
                        "cmd", "OFFER",
                        "tripId", trip.getTripUuid().toString(),
                        "driverId", driverId,
                        "origin", Map.of("lat", trip.getOriginLat(), "lng", trip.getOriginLng())
                ));
            }
        }
    }
}
