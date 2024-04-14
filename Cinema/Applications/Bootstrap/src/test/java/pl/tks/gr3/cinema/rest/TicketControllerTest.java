package pl.tks.gr3.cinema.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.TicketServiceTicketNotFoundException;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.tickets.ReadTicketUseCase;
import pl.tks.gr3.cinema.ports.userinterface.tickets.WriteTicketUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.viewrest.controllers.TicketController;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketDTO;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketInputDTO;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketSelfInputDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({TicketController.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {TicketController.class}, useDefaultFilters = false)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadTicketUseCase readTicket;

    @MockBean
    private WriteTicketUseCase writeTicket;

    @MockBean
    private ReadUserUseCase<Client> readClient;

    @MockBean
    private JWSUseCase jwsService;

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void createTicketValidInputReturnsCreated() throws Exception {

        Client client = new Client(UUID.randomUUID(), "testUser", "test");
        LocalDateTime time = LocalDateTime.now();
        TicketInputDTO ticketInputDTO = new TicketInputDTO(time.toString(), client.getUserID(), UUID.randomUUID());
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket(ticketId, time, 10.0, ticketInputDTO.getClientID(), ticketInputDTO.getMovieID());

        when(readClient.findByUUID(client.getUserID())).thenReturn(client);

        when(writeTicket.create(ticketInputDTO.getMovieTime(), ticketInputDTO.getClientID(), ticketInputDTO.getMovieID())).thenReturn(ticket);

        this.mockMvc.perform(post("/api/v1/tickets")
                        .contentType("application/json")
                        .content("{\"clientID\":\"" + ticketInputDTO.getClientID() + "\",\"movieTime\":\"" + ticketInputDTO.getMovieTime() + "\",\"movieID\":\"" + ticketInputDTO.getMovieID() + "\"}").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ticketID").value(ticketId.toString()));
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void createTicketInvalidInputReturnsBadRequest() throws Exception {
        Client client = new Client(UUID.randomUUID(), "testUser", "test");
        LocalDateTime time = LocalDateTime.now();
        TicketInputDTO ticketInputDTO = new TicketInputDTO(null, null, UUID.randomUUID());
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket(ticketId, time, 10.0, ticketInputDTO.getClientID(), ticketInputDTO.getMovieID());

        when(readClient.findByUUID(client.getUserID())).thenReturn(client);

        when(writeTicket.create(ticketInputDTO.getMovieTime(), ticketInputDTO.getClientID(), ticketInputDTO.getMovieID())).thenReturn(ticket);

        this.mockMvc.perform(post("/api/v1/tickets")
                        .contentType("application/json")
                        .content("{\"clientID\":\"" + ticketInputDTO.getClientID() + "\",\"movieTime\":\"" + ticketInputDTO.getMovieTime() + "\",\"movieID\":\"" + ticketInputDTO.getMovieID() + "\"}").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void createSelfTicketValidInputReturnsCreated() throws Exception {

        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "test123456");
        LocalDateTime time = LocalDateTime.now();
        TicketInputDTO ticketInputDTO = new TicketInputDTO(time.toString(), client.getUserID(), UUID.randomUUID());
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket(ticketId, time, 10.0, ticketInputDTO.getClientID(), ticketInputDTO.getMovieID());

        when(readClient.findByLogin(client.getUserLogin())).thenReturn(client);

        when(writeTicket.create(ticketInputDTO.getMovieTime(), ticketInputDTO.getClientID(), ticketInputDTO.getMovieID())).thenReturn(ticket);

        this.mockMvc.perform(post("/api/v1/tickets/self")
                        .contentType("application/json")
                        .content("{\"clientID\":\"" + ticketInputDTO.getClientID() + "\",\"movieTime\":\"" + ticketInputDTO.getMovieTime() + "\",\"movieID\":\"" + ticketInputDTO.getMovieID() + "\"}").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ticketID").value(ticketId.toString()));
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findAllTicketsReturnsListOfTickets() throws Exception {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket(UUID.randomUUID(), LocalDateTime.now(), 0.0, UUID.randomUUID(), UUID.randomUUID()));
        tickets.add(new Ticket(UUID.randomUUID(), LocalDateTime.now(), 0.0, UUID.randomUUID(), UUID.randomUUID()));
        when(readTicket.findAll()).thenReturn(tickets);

        mockMvc.perform(get("/api/v1/tickets/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketID").exists())
                .andExpect(jsonPath("$[1].ticketID").exists());
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findAllTicketsReturnsEmptyListOfTickets() throws Exception {
        when(readTicket.findAll()).thenThrow(new TicketServiceTicketNotFoundException("No tickets found", new Throwable()));

        mockMvc.perform(get("/api/v1/tickets/all"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void findByUUIDValidUUIDReturnsTicket() throws Exception {
        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "test");

        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), 10.0, client.getUserID(), UUID.randomUUID());

        when(readClient.findByLogin(client.getUserLogin())).thenReturn(client);

        when(readTicket.findByUUID(ticketId)).thenReturn(ticket);

        mockMvc.perform(get("/api/v1/tickets/{id}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketID").value(ticketId.toString()));
    }

    @WithMockUser(username = "StaffLoginNo1", roles = {"STAFF"})
    @Test
    public void findByUUIDInvalidUUIDReturnsNotFound() throws Exception {
        UUID ticketId = UUID.randomUUID();
        when(readTicket.findByUUID(ticketId)).thenThrow(new TicketServiceTicketNotFoundException("Ticket not found", new Throwable()));

        mockMvc.perform(get("/api/v1/tickets/{id}", ticketId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void deleteTicketValidUUIDReturnsNoContent() throws Exception {
        UUID ticketId = UUID.randomUUID();
        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "test123456");
        Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), 10.0, client.getUserID(), UUID.randomUUID());

        when(readTicket.findByUUID(ticketId)).thenReturn(ticket);
        when(readClient.findByLogin(client.getUserLogin())).thenReturn(client);

        mockMvc.perform(delete("/api/v1/tickets/{id}/delete", ticketId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(writeTicket, times(1)).delete(ticketId);
    }

    @WithMockUser(username = "ClientLoginNo1", roles = {"CLIENT"})
    @Test
    public void deleteTicketInvalidUUIDReturnsBadRequest() throws Exception {
        UUID invalidTicketId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "test123456");

        Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), 10.0, client.getUserID(), UUID.randomUUID());

        when(readTicket.findByUUID(invalidTicketId)).thenReturn(ticket);
        when(readClient.findByLogin(client.getUserLogin())).thenReturn(client);

        doThrow(new GeneralServiceException("Ticket not found", new Throwable())).when(writeTicket).delete(invalidTicketId);

        mockMvc.perform(delete("/api/v1/tickets/{id}/delete", invalidTicketId).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "ClientLoginNo2", roles = {"CLIENT"})
    @Test
    public void deleteOtherUsersTicketReturnsForbidden() throws Exception {
        UUID invalidTicketId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        Client client = new Client(UUID.randomUUID(), "ClientLoginNo1", "test123456");

        Client client2 = new Client(UUID.randomUUID(), "ClientLoginNo2", "test123456");

        Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), 10.0, client.getUserID(), UUID.randomUUID());

        when(readTicket.findByUUID(invalidTicketId)).thenReturn(ticket);
        when(readClient.findByLogin(client2.getUserLogin())).thenReturn(client2);
        when(readClient.findByLogin(client.getUserLogin())).thenReturn(client);

        doThrow(new GeneralServiceException("Ticket not found", new Throwable())).when(writeTicket).delete(invalidTicketId);

        mockMvc.perform(delete("/api/v1/tickets/{id}/delete", invalidTicketId).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
