package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.MovieServiceMovieNotFoundException;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.ports.userinterface.movies.ReadMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.movies.WriteMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.MovieController;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieInputDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({MovieController.class})
@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = {MovieController.class}, useDefaultFilters = false)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadMovieUseCase readMovie;

    @MockBean
    private WriteMovieUseCase writeMovie;

    @MockBean
    private JWSUseCase jwsService;

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void createMovieValidInputReturnsCreated() throws Exception {
        MovieInputDTO movieInputDTO = new MovieInputDTO("Test Movie", 10.0, 1, 100);
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats());

        when(writeMovie.create(movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats())).thenReturn(movie);

        this.mockMvc.perform(post("/api/v1/movies")
                        .contentType("application/json")
                        .content("{\"movieTitle\":\"Test Movie\",\"movieBasePrice\":10.0,\"scrRoomNumber\":1,\"numberOfAvailableSeats\":100}").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.movieID").value(movieId.toString()));
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void createMovieInvalidInputReturnsBadRequest() throws Exception {
        MovieInputDTO movieInputDTO = new MovieInputDTO("", 10.0, 1, 100);
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats());

        when(writeMovie.create(movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats())).thenThrow(new MovieServiceMovieNotFoundException("Movie creation failed", new Throwable()));

        this.mockMvc.perform(post("/api/v1/movies")
                        .contentType("application/json")
                        .content("{\"movieTitle\":\"\",\"movieBasePrice\":10.0,\"scrRoomNumber\":1,\"numberOfAvailableSeats\":100}").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findAllMoviesReturnsListOfMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(UUID.randomUUID(), "Test Movie 1", 10.0, 1, 100));
        movies.add(new Movie(UUID.randomUUID(), "Test Movie 2", 8.0, 2, 80));
        when(readMovie.findAll()).thenReturn(movies);

        mockMvc.perform(get("/api/v1/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieID").exists())
                .andExpect(jsonPath("$[1].movieID").exists());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findAllMoviesReturnsEmptyListOfMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        when(readMovie.findAll()).thenThrow(new MovieServiceMovieNotFoundException("No movies found", new Throwable()));

        mockMvc.perform(get("/api/v1/movies/all"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findByUUIDValidUUIDReturnsMovie() throws Exception {
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, "Test Movie", 10.0, 1, 100);
        when(readMovie.findByUUID(movieId)).thenReturn(movie);

        mockMvc.perform(get("/api/v1/movies/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieID").value(movieId.toString()));
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findByUUIDInvalidUUIDReturnsNotFound() throws Exception {
        UUID movieId = UUID.randomUUID();
        when(readMovie.findByUUID(movieId)).thenThrow(new MovieServiceMovieNotFoundException("Movie not found", new Throwable()));

        mockMvc.perform(get("/api/v1/movies/{id}", movieId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findAllTicketsForCertainMovieReturnsListOfTickets() throws Exception {

        UUID movieId = UUID.randomUUID();
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket(UUID.randomUUID(), LocalDateTime.now(), 10.0, UUID.randomUUID(), movieId));
        tickets.add(new Ticket(UUID.randomUUID(), LocalDateTime.now(), 8.0, UUID.randomUUID(), movieId));
        when(readMovie.getListOfTickets(movieId)).thenReturn(tickets);

        mockMvc.perform(get("/api/v1/movies/{id}/tickets", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketID").exists())
                .andExpect(jsonPath("$[1].ticketID").exists());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void deleteMovieValidUUIDReturnsNoContent() throws Exception {
        MovieInputDTO movieInputDTO = new MovieInputDTO("Test Movie", 10.0, 1, 100);
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats());

        when(writeMovie.create(movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats())).thenReturn(movie);

        this.mockMvc.perform(delete("/api/v1/movies/{id}/delete", movieId).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void deleteMovieInvalidUUIDReturnsBadRequest() throws Exception {
        UUID invalidMovieId = UUID.randomUUID();

        doThrow(new GeneralServiceException("Movie not found", new Throwable())).when(writeMovie).delete(invalidMovieId);

        mockMvc.perform(delete("/api/v1/movies/{id}/delete", invalidMovieId).with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
