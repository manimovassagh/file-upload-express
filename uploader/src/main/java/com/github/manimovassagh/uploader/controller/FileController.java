package com.github.manimovassagh.uploader.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.manimovassagh.uploader.config.FileUploadConfig;
import com.github.manimovassagh.uploader.model.FileUploadResponse;
import com.github.manimovassagh.uploader.service.FileStorageService;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileStorageService fileStorageService;
    private final FileUploadConfig fileUploadConfig;

    public FileController(FileStorageService fileStorageService, FileUploadConfig fileUploadConfig) {
        this.fileStorageService = fileStorageService;
        this.fileUploadConfig = fileUploadConfig;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body(new FileUploadResponse("No files uploaded", List.of()));
        }

        if (files.length > fileUploadConfig.getMaxFiles()) {
            return ResponseEntity.badRequest().body(new FileUploadResponse(
                "Too many files. Maximum is " + fileUploadConfig.getMaxFiles() + " files.", 
                List.of()
            ));
        }

        for (MultipartFile file : files) {
            if (file.getSize() > fileUploadConfig.getMaxFileSize()) {
                return ResponseEntity.badRequest().body(new FileUploadResponse(
                    "File too large. Maximum size is " + (fileUploadConfig.getMaxFileSize() / (1024 * 1024)) + "MB.", 
                    List.of()
                ));
            }

            if (!fileStorageService.isValidFileType(file.getContentType())) {
                return ResponseEntity.badRequest().body(new FileUploadResponse("Invalid file type. Only images, PDFs, and documents are allowed.", List.of()));
            }
        }

        FileUploadResponse response = fileStorageService.storeFiles(files);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = fileStorageService.listFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
} 