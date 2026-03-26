package com.videostore.videostore.application.port.out;

public interface ImageStoragePort {
    String upload(byte[] file, String filename);

    void delete(String url);
}