package pl.tks.gr3.cinema.viewrest.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.MovieServiceMovieNotFoundException;
import pl.tks.gr3.cinema.viewrest.api.MovieControllerInterface;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.userinterface.movies.ReadMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.movies.WriteMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieDTO;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieInputDTO;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController implements MovieControllerInterface {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final ReadMovieUseCase readMovie;
    private final WriteMovieUseCase writeMovie;
    private final JWSUseCase jwsService;

    @Autowired
    public MovieController(ReadMovieUseCase readMovie,
                           WriteMovieUseCase writeMovie,
                           JWSUseCase jwsService) {
        this.readMovie = readMovie;
        this.writeMovie = writeMovie;
        this.jwsService = jwsService;
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> create(@RequestBody MovieInputDTO movieInputDTO) {
        try {
            Movie movie = this.writeMovie.create(movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats());

            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/movies/" + movieDTO.getMovieID().toString())).contentType(MediaType.APPLICATION_JSON).body(movieDTO);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) or hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).CLIENT)")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<Movie> listOfFoundMovies = this.readMovie.findAll();
            List<MovieDTO> listOfDTOs = new ArrayList<>();
            for (Movie movie : listOfFoundMovies) {
                listOfDTOs.add(new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats()));
            }

            if (listOfDTOs.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) or hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).CLIENT)")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID movieID) {
        try {
            Movie movie = this.readMovie.findByUUID(movieID);
            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            return ResponseEntity.ok().header(HttpHeaders.ETAG, jwsService.generateSignatureForMovie(movie)).contentType(MediaType.APPLICATION_JSON).body(movieDTO);
        } catch (MovieServiceMovieNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF)")
    @GetMapping(value = "{id}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllTicketsForCertainMovie(@PathVariable("id") UUID movieID) {
        List<Ticket> listOfTickets = this.readMovie.getListOfTickets(movieID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTickets) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID()));
        }

        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF)")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody MovieDTO movieDTO) {
        try {
            Movie movie = new Movie(movieDTO.getMovieID(), movieDTO.getMovieTitle(), movieDTO.getMovieBasePrice(), movieDTO.getScrRoomNumber(), movieDTO.getNumberOfAvailableSeats());

            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            if (jwsService.verifyMovieSignature(ifMatch.replace("\"", ""), movie)) {
                this.writeMovie.update(movie);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Signature and given object does not match.");
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF)")
    @DeleteMapping(value = "/{id}/delete")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") UUID movieID) {
        try {
            this.writeMovie.delete(movieID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
