package com.videostore.videostore.application.usecase.movie;

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

@Service
public class UpdateMoviePosterUseCaseImpl implements UpdateMoviePosterUseCase {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final ImageStoragePort imageStoragePort;

    public UpdateMoviePosterUseCaseImpl(MovieRepository movieRepository,
                                        ReviewRepository reviewRepository,
                                        ImageStoragePort imageStoragePort) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    @Transactional
    public MovieDetails execute(Long id, UpdateMoviePosterCommand command) {
        MovieId movieId = new MovieId(id);
        Movie movie = movieRepository.findById(new MovieId(id))
                .orElseThrow(() -> new MovieNotFoundException(id));

        if (movie.getPosterUrl() != null) {
            imageStoragePort.delete(movie.getPosterUrl().value());
            movie.setPosterUrl(null);
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
}
