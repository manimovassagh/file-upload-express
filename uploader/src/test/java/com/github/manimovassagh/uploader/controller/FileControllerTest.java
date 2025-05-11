package com.github.manimovassagh.uploader.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.github.manimovassagh.uploader.config.FileUploadConfig;
import com.github.manimovassagh.uploader.model.FileUploadResponse;
import com.github.manimovassagh.uploader.service.FileStorageService;

class FileControllerTest {

    private FileController fileController;
    private FileStorageService fileStorageService;
    private FileUploadConfig fileUploadConfig;

    @BeforeEach
    void setUp() {
        fileStorageService = mock(FileStorageService.class);
        fileUploadConfig = mock(FileUploadConfig.class);
        when(fileUploadConfig.getMaxFileSize()).thenReturn(5L * 1024 * 1024); // 5MB
        when(fileUploadConfig.getMaxFiles()).thenReturn(5);
        fileController = new FileController(fileStorageService, fileUploadConfig);
    }

    @Test
    void uploadFiles_ShouldReturnSuccess_WhenValidFilesProvided() {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
            "files",
            "test1.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "files",
            "test2.pdf",
            "application/pdf",
            "test pdf content".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        FileUploadResponse.FileInfo fileInfo1 = new FileUploadResponse.FileInfo(
            "test1.jpg",
            "test1.jpg",
            1000L,
            "image/jpeg"
        );
        FileUploadResponse.FileInfo fileInfo2 = new FileUploadResponse.FileInfo(
            "test2.pdf",
            "test2.pdf",
            2000L,
            "application/pdf"
        );
        FileUploadResponse expectedResponse = new FileUploadResponse(
            "Files uploaded successfully",
            Arrays.asList(fileInfo1, fileInfo2)
        );

        when(fileStorageService.isValidFileType(any())).thenReturn(true);
        when(fileStorageService.storeFiles(any())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<FileUploadResponse> response = fileController.uploadFiles(files);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Files uploaded successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getFiles().size());
    }

    @Test
    void uploadFiles_ShouldReturnBadRequest_WhenNoFilesProvided() {
        // Arrange
        MockMultipartFile[] files = new MockMultipartFile[0];

        // Act
        ResponseEntity<FileUploadResponse> response = fileController.uploadFiles(files);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No files uploaded", response.getBody().getMessage());
        assertTrue(response.getBody().getFiles().isEmpty());
    }

    @Test
    void uploadFiles_ShouldReturnBadRequest_WhenTooManyFilesProvided() {
        // Arrange
        MockMultipartFile[] files = new MockMultipartFile[6];
        for (int i = 0; i < 6; i++) {
            files[i] = new MockMultipartFile(
                "files",
                "test" + i + ".jpg",
                "image/jpeg",
                "test content".getBytes()
            );
        }

        // Act
        ResponseEntity<FileUploadResponse> response = fileController.uploadFiles(files);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Too many files. Maximum is 5 files.", response.getBody().getMessage());
        assertTrue(response.getBody().getFiles().isEmpty());
    }

    @Test
    void uploadFiles_ShouldReturnBadRequest_WhenFileTypeIsInvalid() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "test.js",
            "application/javascript",
            "test content".getBytes()
        );
        MockMultipartFile[] files = {file};

        when(fileStorageService.isValidFileType(any())).thenReturn(false);

        // Act
        ResponseEntity<FileUploadResponse> response = fileController.uploadFiles(files);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid file type. Only images, PDFs, and documents are allowed.", response.getBody().getMessage());
        assertTrue(response.getBody().getFiles().isEmpty());
    }

    @Test
    void listFiles_ShouldReturnListOfFiles() {
        // Arrange
        List<String> expectedFiles = Arrays.asList("file1.jpg", "file2.pdf");
        when(fileStorageService.listFiles()).thenReturn(expectedFiles);

        // Act
        ResponseEntity<List<String>> response = fileController.listFiles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedFiles, response.getBody());
    }

    @Test
    void downloadFile_ShouldReturnFileResource() {
        // Arrange
        String filename = "test.jpg";
        Resource resource = new ByteArrayResource("test content".getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        when(fileStorageService.loadFileAsResource(filename)).thenReturn(resource);

        // Act
        ResponseEntity<Resource> response = fileController.downloadFile(filename);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().containsKey("Content-Disposition"));
        assertEquals("attachment; filename=\"" + filename + "\"", 
            response.getHeaders().getFirst("Content-Disposition"));
    }
} 