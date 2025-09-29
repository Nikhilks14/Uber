package com.Taxi.DriverService.model;

import com.Taxi.DriverService.enums.Availability;
import com.Taxi.DriverService.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="drivers")
@Getter
@Setter
@NoArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false, unique=true)
    private Long userId;

    private String fullName;
    private String phone;
    private String email;

    private Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.PENDING; // PENDING, ACTIVE, SUSPENDED

    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.OFFLINE; // OFFLINE, ONLINE, ON_TRIP

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
