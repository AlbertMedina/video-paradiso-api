package com.videostore.videostore.application.command.movie;

public record UpdateMoviePosterCommand(
        byte[] poster,
        String posterFilename
) {
}
