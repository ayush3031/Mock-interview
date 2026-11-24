package com.mockinterview.backend.repository;

import com.mockinterview.backend.model.UploadedResume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedResumeRepository extends JpaRepository<UploadedResume, Long> {
}