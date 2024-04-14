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
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.ports.userinterface.movies.ReadMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.movies.WriteMovieUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.MovieController;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieInputDTO;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MovieController.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {MovieController.class}, useDefaultFilters = false)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadMovieUseCase readMovie;

    @MockBean
    private WriteMovieUseCase writeMovie;

    @MockBean
    private JWSUseCase jwsUseCase;
}
