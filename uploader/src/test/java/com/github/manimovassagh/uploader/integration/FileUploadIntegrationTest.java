package com.github.manimovassagh.uploader.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class FileUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    static Path tempUploadDir;

    @DynamicPropertySource
    static void setUploadDir(DynamicPropertyRegistry registry) {
        registry.add("file.upload.dir", () -> tempUploadDir.toString());
    }

    @Test
    void shouldUploadAndDownloadFile() throws Exception {
        // Create test file
        String filename = "test.txt";
        String content = "Hello, World!";
        MockMultipartFile file = new MockMultipartFile(
            "files",
            filename,
            "text/plain",
            content.getBytes()
        );

        // Upload file
        MvcResult uploadResult = mockMvc.perform(multipart("/api/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Files uploaded successfully"))
                .andExpect(jsonPath("$.files[0].originalName").value(filename))
                .andReturn();

        // Parse stored filename from response
        String uploadResponse = uploadResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(uploadResponse);
        String storedFilename = root.get("files").get(0).get("filename").asText();

        // List files
        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        // Download file using stored filename
        MvcResult downloadResult = mockMvc.perform(get("/api/files/" + storedFilename))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        // Verify file content
        String downloadedContent = downloadResult.getResponse().getContentAsString();
        assertEquals(content, downloadedContent);
    }

    @Test
    void shouldRejectInvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "test.js",
            "application/javascript",
            "console.log('test')".getBytes()
        );

        mockMvc.perform(multipart("/api/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid file type. Only images, PDFs, and documents are allowed."));
    }

    @Test
    void shouldRejectTooManyFiles() throws Exception {
        MockMultipartFile[] files = new MockMultipartFile[6];
        for (int i = 0; i < 6; i++) {
            files[i] = new MockMultipartFile(
                "files",
                "test" + i + ".jpg",
                "image/jpeg",
                "test content".getBytes()
            );
        }

        mockMvc.perform(multipart("/api/upload")
                .file(files[0])
                .file(files[1])
                .file(files[2])
                .file(files[3])
                .file(files[4])
                .file(files[5]))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Too many files. Maximum is 5 files."));
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "empty.txt",
            "text/plain",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/upload")
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForNonExistentFile() throws Exception {
        mockMvc.perform(get("/api/files/nonexistent.txt"))
                .andExpect(status().isNotFound());
    }
} 