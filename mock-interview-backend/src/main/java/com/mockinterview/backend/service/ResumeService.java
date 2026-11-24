package com.mockinterview.backend.service;

import com.mockinterview.backend.model.UploadedResume;
import com.mockinterview.backend.repository.UploadedResumeRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
public class ResumeService {

    private final UploadedResumeRepository repository;
    private final Tika tika = new Tika();

    public ResumeService(UploadedResumeRepository repository) {
        this.repository = repository;
    }

    public UploadedResume extractAndSave(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isAllowed(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only PDF, DOCX, or TXT allowed");
        }

        // parseToString declares throws IOException, TikaException.
        // IOException bubbles up (declared). TikaException must be handled here —
        // a corrupt file is a client problem (422), not a server error (500).
        String text;
        try {
            text = tika.parseToString(file.getInputStream());
        } catch (TikaException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Could not parse file: " + e.getMessage(), e);
        }

        if (text == null || text.isBlank()) {
            // Scanned PDF / wrong encoding / empty document → client problem.
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "No text could be extracted — file may be scanned or corrupted");
        }

        UploadedResume resume = new UploadedResume();
        resume.setOriginalFilename(file.getOriginalFilename());
        resume.setContentType(contentType);
        resume.setSizeBytes(file.getSize());
        resume.setRawText(text);
        resume.setUploadedAt(OffsetDateTime.now());

        return repository.save(resume);
    }

    private boolean isAllowed(String contentType) {
        return contentType.equals("application/pdf")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equals("text/plain");
    }
}