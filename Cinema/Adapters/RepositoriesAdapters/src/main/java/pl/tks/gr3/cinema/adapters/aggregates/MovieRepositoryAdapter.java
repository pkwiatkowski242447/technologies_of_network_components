package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.converters.MovieConverter;
import pl.tks.gr3.cinema.adapters.converters.TicketConverter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.api.MovieRepositoryInterface;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.infrastructure.movies.CreateMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.DeleteMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.ReadMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.UpdateMoviePort;

import java.util.List;
import java.util.UUID;

@Component
public class MovieRepositoryAdapter implements CreateMoviePort, ReadMoviePort, UpdateMoviePort, DeleteMoviePort {

    private final MovieRepositoryInterface movieRepository;


    @Autowired
    public MovieRepositoryAdapter(MovieRepositoryInterface movieRepository) {
        this.movieRepository = movieRepository;
    }

    // C

    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieRepositoryException {
        return MovieConverter.convertToMovie(movieRepository.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    // R

    @Override
    public List<Movie> findAll() throws MovieRepositoryException {
        return movieRepository.findAll().stream().map(MovieConverter::convertToMovie).toList();
    }

    @Override
    public Movie findByUUID(UUID movieID) throws MovieRepositoryException {
        return MovieConverter.convertToMovie(movieRepository.findByUUID(movieID));
    }

    @Override
    public List<Ticket> getListOfTickets(UUID movieID) {
        return this.movieRepository.getListOfTicketsForMovie(movieID).stream().map(TicketConverter::convertToTicket).toList();
    }

    // U

    @Override
    public void update(Movie movie) throws MovieRepositoryException {
        movieRepository.update(MovieConverter.convertToMovieEnt(movie));
    }

    // D

    @Override
    public void delete(UUID movieID) throws MovieRepositoryException {
        movieRepository.delete(movieID);
    }
}
