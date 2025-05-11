package com.github.manimovassagh.uploader.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.github.manimovassagh.uploader.config.FileUploadConfig;
import com.github.manimovassagh.uploader.model.FileUploadResponse;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;
    private FileUploadConfig fileUploadConfig;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir);
    }

    @Test
    void storeFiles_ShouldStoreFilesSuccessfully() throws IOException {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
            "file1",
            "test1.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file2",
            "test2.pdf",
            "application/pdf",
            "test pdf content".getBytes()
        );
        MultipartFile[] files = {file1, file2};

        // Act
        FileUploadResponse response = fileStorageService.storeFiles(files);

        // Assert
        assertNotNull(response);
        assertEquals("Files uploaded successfully", response.getMessage());
        assertEquals(2, response.getFiles().size());
        
        // Verify files were actually stored
        List<String> storedFiles = fileStorageService.listFiles();
        assertEquals(2, storedFiles.size());
    }

    @Test
    void storeFiles_ShouldThrowException_WhenFileIsEmpty() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty",
            "empty.txt",
            "text/plain",
            new byte[0]
        );
        MultipartFile[] files = {emptyFile};

        // Act & Assert
        assertThrows(RuntimeException.class, () -> fileStorageService.storeFiles(files));
    }

    @Test
    void listFiles_ShouldReturnEmptyList_WhenNoFiles() {
        // Act
        List<String> files = fileStorageService.listFiles();

        // Assert
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void loadFileAsResource_ShouldReturnResource_WhenFileExists() throws IOException {
        // Arrange
        String filename = "test.txt";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "test content".getBytes());

        // Act
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Assert
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(filename, resource.getFilename());
    }

    @Test
    void loadFileAsResource_ShouldThrowException_WhenFileDoesNotExist() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> fileStorageService.loadFileAsResource("nonexistent.txt"));
    }

    @Test
    void isValidFileType_ShouldReturnTrue_ForValidFileTypes() {
        // Arrange
        String[] validTypes = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        };

        // Act & Assert
        for (String type : validTypes) {
            assertTrue(fileStorageService.isValidFileType(type));
        }
    }

    @Test
    void isValidFileType_ShouldReturnFalse_ForInvalidFileTypes() {
        // Arrange
        String[] invalidTypes = {
            "application/javascript",
            "text/html",
            "application/xml",
            "video/mp4"
        };

        // Act & Assert
        for (String type : invalidTypes) {
            assertFalse(fileStorageService.isValidFileType(type));
        }
    }
} 