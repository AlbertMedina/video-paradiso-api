package com.videostore.videostore.application.exception;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message) {
        super("Error uploading movie poster to Cloudinary: " + message);
    }
}
