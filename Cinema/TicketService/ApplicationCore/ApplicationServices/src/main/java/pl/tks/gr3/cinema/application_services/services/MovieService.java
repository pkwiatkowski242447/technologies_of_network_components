package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.MovieRepositoryMovieNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.infrastructure.movies.CreateMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.DeleteMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.ReadMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.UpdateMoviePort;
import pl.tks.gr3.cinema.ports.userinterface.movies.ReadMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.movies.WriteMovieUseCase;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService implements ReadMovieUseCase, WriteMovieUseCase {

    private final CreateMoviePort createMoviePort;
    private final ReadMoviePort readMoviePort;
    private final UpdateMoviePort updateMoviePort;
    private final DeleteMoviePort deleteMoviePort;


    @Autowired
    public MovieService(CreateMoviePort createMoviePort,
                        ReadMoviePort readMoviePort,
                        UpdateMoviePort updateMoviePort,
                        DeleteMoviePort deleteMoviePort) {
        this.createMoviePort = createMoviePort;
        this.readMoviePort = readMoviePort;
        this.updateMoviePort = updateMoviePort;
        this.deleteMoviePort = deleteMoviePort;
    }


    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieServiceCreateException {
        try {
            return this.createMoviePort.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Movie findByUUID(UUID movieID) throws MovieServiceReadException {
        try {
            return this.readMoviePort.findByUUID(movieID);
        } catch (MovieRepositoryMovieNotFoundException exception) {
            throw new MovieServiceMovieNotFoundException(exception.getMessage(), exception);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Movie> findAll() throws MovieServiceReadException {
        try {
            return this.readMoviePort.findAll();
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Movie movie) throws MovieServiceUpdateException {
        try {
            this.updateMoviePort.update(movie);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID movieID) throws MovieServiceDeleteException {
        try {
            this.deleteMoviePort.delete(movieID);
        } catch (MovieRepositoryException exception) {
            throw new MovieServiceDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getListOfTickets(UUID movieID) {
        return this.readMoviePort.getListOfTickets(movieID);
    }
}
