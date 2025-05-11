package com.github.manimovassagh.uploader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.max-size:5242880}") // 5MB in bytes
    private long maxFileSize;

    @Value("${file.upload.max-files:5}")
    private int maxFiles;

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public String getUploadDir() {
        return uploadDir;
    }
} 