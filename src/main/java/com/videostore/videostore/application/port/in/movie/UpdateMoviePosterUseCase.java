package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.application.command.movie.UpdateMoviePosterCommand;
import com.videostore.videostore.application.model.MovieDetails;

public interface UpdateMoviePosterUseCase {
    MovieDetails execute(Long id, UpdateMoviePosterCommand command);
}
