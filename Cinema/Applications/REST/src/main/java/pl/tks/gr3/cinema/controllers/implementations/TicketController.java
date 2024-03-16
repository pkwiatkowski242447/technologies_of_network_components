package pl.tks.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceReadException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.ticket.TicketServiceTicketNotFoundException;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.application_services.services.TicketService;
import pl.tks.gr3.cinema.domain_model.model.Ticket;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.dto.input.TicketInputDTO;
import pl.tks.gr3.cinema.dto.input.TicketSelfInputDTO;
import pl.tks.gr3.cinema.dto.output.TicketDTO;
import pl.tks.gr3.cinema.security.services.JWSService;
import pl.tks.gr3.cinema.controllers.interfaces.TicketControllerInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController implements TicketControllerInterface {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final TicketService ticketService;
    private final ClientService clientService;
    private final JWSService jwsService;

    @Autowired
    public TicketController(TicketService ticketService, ClientService clientService, JWSService jwsService) {
        this.ticketService = ticketService;
        this.clientService = clientService;
        this.jwsService = jwsService;
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> create(@RequestBody TicketInputDTO ticketInputDTO) {
        try {
            Client client = this.clientService.findByUUID(ticketInputDTO.getClientID());
            Ticket ticket = this.ticketService.create(ticketInputDTO.getMovieTime(), client.getUserID(), ticketInputDTO.getMovieID());

            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/tickets/" + ticketDTO.getTicketID().toString())).contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).CLIENT)")
    @PostMapping(value = "/self", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody TicketSelfInputDTO ticketSelfInputDTO) {
        try {
            Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            Ticket ticket = this.ticketService.create(ticketSelfInputDTO.getMovieTime(), client.getUserID(), ticketSelfInputDTO.getMovieID());

            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/tickets/" + ticketDTO.getTicketID().toString())).contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF) or hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).CLIENT)")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID ticketID) {
        try {
            Ticket ticket = this.ticketService.findByUUID(ticketID);
            try {
                Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
                if (client.getUserID().equals(ticket.getUserID())) {
                    TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID());
                    return ResponseEntity.ok().header(HttpHeaders.ETAG, jwsService.generateSignatureForTicket(ticket)).contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("This ticket does not belong to you.");
                }
            } catch (ClientServiceReadException exception) {
                TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
            }
        } catch (TicketServiceTicketNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF)")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<Ticket> listOfFoundTickets = this.ticketService.findAll();
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfFoundTickets) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID()));
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

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).CLIENT)")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody TicketDTO ticketDTO) {
        try {
            Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            Ticket ticket = this.ticketService.findByUUID(ticketDTO.getTicketID());
            if (!ticket.getUserID().equals(client.getUserID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("This ticket belongs to other user.");
            }

            ticket.setMovieTime(ticketDTO.getMovieTime());

            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().body(messages);
            }

            if (jwsService.verifyTicketSignature(ifMatch.replace("\"", ""), ticket)) {
                this.ticketService.update(ticket);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Signature and given object does not match.");
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).CLIENT)")
    @DeleteMapping(value = "/{id}/delete")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") UUID ticketID) {
        try {
            Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            Ticket ticket =  this.ticketService.findByUUID(ticketID);
            if (!ticket.getUserID().equals(client.getUserID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("This ticket belongs to other user.");
            } else {
                this.ticketService.delete(ticketID);
                return ResponseEntity.noContent().build();
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
