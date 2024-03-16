package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.MovieRepositoryMovieNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.userinterface.MovieServiceInterface;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService implements MovieServiceInterface {

    private MovieRepositoryAdapter movieRepository;

    public MovieService() {
    }

    @Autowired
    public MovieService(MovieRepositoryAdapter movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieServiceCreateException {
        try {
            return this.movieRepository.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Movie findByUUID(UUID movieID) throws MovieServiceReadException {
        try {
            return this.movieRepository.findByUUID(movieID);
        } catch (MovieRepositoryMovieNotFoundException exception) {
            throw new MovieServiceMovieNotFoundException(exception.getMessage(), exception);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Movie> findAll() throws MovieServiceReadException {
        try {
            return this.movieRepository.findAll();
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Movie movie) throws MovieServiceUpdateException {
        try {
            this.movieRepository.update(movie);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID movieID) throws MovieServiceDeleteException {
        try {
            this.movieRepository.delete(movieID);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getListOfTickets(UUID movieID) {
        return this.movieRepository.getListOfTickets(movieID);
    }
}
