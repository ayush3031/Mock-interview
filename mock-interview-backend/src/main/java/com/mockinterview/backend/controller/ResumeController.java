package com.mockinterview.backend.controller;

import com.mockinterview.backend.model.UploadedResume;
import com.mockinterview.backend.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        UploadedResume saved = resumeService.extractAndSave(file);
        return ResponseEntity.ok(Map.of(
                "resumeId", saved.getId(),
                "originalFilename", saved.getOriginalFilename(),
                "charCount", saved.getRawText().length()
        ));
    }
}