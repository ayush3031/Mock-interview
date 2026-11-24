package com.mockinterview.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "uploaded_resume")
public class UploadedResume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rawText;

    @Column(nullable = false)
    private OffsetDateTime uploadedAt;

    // getters + setters + no-arg constructor (JPA)
}