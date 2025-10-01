package com.Uber.TripService.service;


import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.geo.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisGeoService {
    private final RedisTemplate<String, String> redis;

    private static final String GEO_KEY = "drivers:geo";
    private static final String DRIVER_META_PREFIX = "driver:%s:meta";

    public void updateDriverLocation(Long driverId, double lat, double lon) {
        redis.opsForGeo().add(GEO_KEY, new Point(lon, lat), String.valueOf(driverId));
        String metaKey = String.format(DRIVER_META_PREFIX, driverId);
        redis.opsForHash().put(metaKey, "lastSeen", Instant.now().toString());
        redis.opsForHash().put(metaKey, "lat", String.valueOf(lat));
        redis.opsForHash().put(metaKey, "lng", String.valueOf(lon));
        redis.expire(metaKey, Duration.ofSeconds(30));
    }

    public List<Long> findNearby(double lat, double lon, double radiusKm, int limit) {
        Circle circle = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redis.opsForGeo().radius(GEO_KEY, circle);
        if (results == null) return List.of();
        return results.getContent().stream()
                .map(r -> Long.parseLong(r.getContent().getName()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean setOfferIfAbsent(String key, long seconds) {
        Boolean ok = redis.opsForValue().setIfAbsent(key, "PENDING", Duration.ofSeconds(seconds));
        return Boolean.TRUE.equals(ok);
    }

    public void removeOffer(String key) {
        redis.delete(key);
    }
}
