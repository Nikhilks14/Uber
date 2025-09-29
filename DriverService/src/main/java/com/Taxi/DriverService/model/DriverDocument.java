package com.Taxi.DriverService.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="driver_documents")
@Getter @Setter @NoArgsConstructor
public class DriverDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    private String docType; // LICENSE, RC, INSURANCE
    private String objectKey; // s3 key
    private boolean verified = false;
    private String verifierComment;
    private Instant uploadedAt = Instant.now();
}
