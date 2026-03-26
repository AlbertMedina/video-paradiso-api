package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.port.in.movie.RemoveMovieUseCase;
import com.videostore.videostore.application.port.out.ImageStoragePort;
import com.videostore.videostore.domain.exception.conflict.MovieHasActiveRentalsException;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveMovieUseCaseImpl implements RemoveMovieUseCase {

    private final MovieRepository movieRepository;
    private final RentalRepository rentalRepository;
    private final ReviewRepository reviewRepository;
    private final FavouriteRepository favouriteRepository;
    private final ImageStoragePort imageStoragePort;

    public RemoveMovieUseCaseImpl(MovieRepository movieRepository,
                                  RentalRepository rentalRepository,
                                  ReviewRepository reviewRepository,
                                  FavouriteRepository favouriteRepository,
                                  ImageStoragePort imageStoragePort
    ) {
        this.movieRepository = movieRepository;
        this.rentalRepository = rentalRepository;
        this.reviewRepository = reviewRepository;
        this.favouriteRepository = favouriteRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    @Transactional
    public void execute(Long movieId) {
        MovieId id = new MovieId(movieId);

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateMovieRemoval(id);

        if (movie.getPosterUrl() != null) {
            imageStoragePort.delete(movie.getPosterUrl().value());
        }

        reviewRepository.removeAllByMovie(id);
        favouriteRepository.removeAllMovie(id);
        movieRepository.removeMovie(id);
    }

    private void validateMovieRemoval(MovieId movieId) {
        if (rentalRepository.countRentalsByMovie(movieId) > 0) {
            throw new MovieHasActiveRentalsException("Cannot remove movie with active rentals");
        }
    }
}