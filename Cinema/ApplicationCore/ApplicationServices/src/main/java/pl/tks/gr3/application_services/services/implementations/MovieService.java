package pl.tks.gr3.application_services.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.*;
import pl.tks.gr3.cinema.exceptions.repositories.MovieRepositoryException;
import pl.tks.gr3.cinema.exceptions.repositories.crud.movie.MovieRepositoryMovieNotFoundException;
import pl.tks.gr3.cinema.model.Movie;
import pl.tks.gr3.cinema.model.Ticket;
import pl.tks.gr3.cinema.repositories.implementations.MovieRepository;
import pl.tks.gr3.cinema.services.interfaces.MovieServiceInterface;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService implements MovieServiceInterface {

    private MovieRepository movieRepository;

    public MovieService() {
    }

    @Autowired
    public MovieService(MovieRepository movieRepository) {
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
    public List<Ticket> getListOfTicketsForCertainMovie(UUID movieID) {
        return this.movieRepository.getListOfTicketsForMovie(movieID);
    }
}
