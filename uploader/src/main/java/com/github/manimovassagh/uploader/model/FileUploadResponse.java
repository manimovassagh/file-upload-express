package com.github.manimovassagh.uploader.model;

import java.util.List;

public class FileUploadResponse {
    private String message;
    private List<FileInfo> files;

    public FileUploadResponse(String message, List<FileInfo> files) {
        this.message = message;
        this.files = files;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public static class FileInfo {
        private String filename;
        private String originalName;
        private long size;
        private String mimeType;

        public FileInfo(String filename, String originalName, long size, String mimeType) {
            this.filename = filename;
            this.originalName = originalName;
            this.size = size;
            this.mimeType = mimeType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
} 