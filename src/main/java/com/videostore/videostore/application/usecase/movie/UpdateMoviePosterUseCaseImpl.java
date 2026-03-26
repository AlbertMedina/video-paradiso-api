package com.videostore.videostore.application.usecase.movie;

import com.cloudinary.Cloudinary;
import com.videostore.videostore.application.command.movie.UpdateMoviePosterCommand;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.UpdateMoviePosterUseCase;
import com.videostore.videostore.application.port.out.ImageStoragePort;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
public class UpdateMoviePosterUseCaseImpl implements UpdateMoviePosterUseCase {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final ImageStoragePort imageStoragePort;
    private final Cloudinary cloudinary;

    public UpdateMoviePosterUseCaseImpl(MovieRepository movieRepository,
                                        ReviewRepository reviewRepository,
                                        ImageStoragePort imageStoragePort,
                                        Cloudinary cloudinary) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
        this.imageStoragePort = imageStoragePort;
        this.cloudinary = cloudinary;
    }

    @Override
    @Transactional
    public MovieDetails execute(Long id, UpdateMoviePosterCommand command) {
        MovieId movieId = new MovieId(id);
        Movie movie = movieRepository.findById(new MovieId(id))
                .orElseThrow(() -> new MovieNotFoundException(id));

        if (movie.getPosterUrl() != null) {
            PosterUrl oldPosterUrl = movie.getPosterUrl();
            movie.setPosterUrl(null);
            try {
                String publicId = extractPublicIdFromUrl(oldPosterUrl.value());
                cloudinary.uploader().destroy(publicId, Map.of());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete movie poster from Cloudinary", e);
            }
        }

        if (command.poster() != null) {
            String posterUrl = imageStoragePort.upload(
                    command.poster(),
                    command.posterFilename()
            );
            movie.setPosterUrl(new PosterUrl(posterUrl));
        }

        Movie updated = movieRepository.updateMovie(movie);

        RatingSummary ratingSummary = reviewRepository.getAverageRatingByMovieId(movieId).orElse(new RatingSummary(0.0, 0));

        return new MovieDetails(
                updated.getId().value(),
                updated.getTitle().value(),
                updated.getYear().value(),
                updated.getGenre().value(),
                updated.getDuration().value(),
                updated.getDirector().value(),
                updated.getSynopsis().value(),
                updated.getNumberOfCopies().value(),
                updated.getPosterUrl() != null ? updated.getPosterUrl().value() : null,
                ratingSummary
        );
    }

    private String extractPublicIdFromUrl(String url) {
        int folderIndex = url.indexOf("/movies/");
        if (folderIndex < 0) {
            throw new IllegalArgumentException("Invalid Cloudinary URL: " + url);
        }
        String pathWithFile = url.substring(folderIndex + 1);
        int dotIndex = pathWithFile.lastIndexOf('.');
        return dotIndex >= 0 ? pathWithFile.substring(0, dotIndex) : pathWithFile;
    }
}
