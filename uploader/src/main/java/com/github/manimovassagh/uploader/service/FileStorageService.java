package com.github.manimovassagh.uploader.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.manimovassagh.uploader.config.FileUploadConfig;
import com.github.manimovassagh.uploader.exception.EmptyFileException;
import com.github.manimovassagh.uploader.exception.FileNotFoundException;
import com.github.manimovassagh.uploader.model.FileUploadResponse;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileUploadConfig fileUploadConfig) {
        this(Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize());
    }

    // Overloaded constructor for testability
    public FileStorageService(Path storageDir) {
        this.fileStorageLocation = storageDir;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileUploadResponse storeFiles(MultipartFile[] files) {
        List<FileUploadResponse.FileInfo> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new EmptyFileException("File name cannot be null");
            }
            originalFilename = StringUtils.cleanPath(originalFilename);
            String filename = System.currentTimeMillis() + "-" + originalFilename;

            try {
                if (file.isEmpty()) {
                    throw new EmptyFileException("Failed to store empty file " + originalFilename);
                }

                if (originalFilename.contains("..")) {
                    throw new RuntimeException("Cannot store file with relative path outside current directory " + originalFilename);
                }

                Path targetLocation = this.fileStorageLocation.resolve(filename);
                Files.copy(file.getInputStream(), targetLocation);

                uploadedFiles.add(new FileUploadResponse.FileInfo(
                    filename,
                    originalFilename,
                    file.getSize(),
                    file.getContentType()
                ));
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + originalFilename, ex);
            }
        }

        return new FileUploadResponse("Files uploaded successfully", uploadedFiles);
    }

    public List<String> listFiles() {
        try {
            return Files.list(fileStorageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Could not list files", ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + filename);
        }
    }

    public boolean isValidFileType(String contentType) {
        List<String> allowedTypes = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        );
        return allowedTypes.contains(contentType);
    }
} 