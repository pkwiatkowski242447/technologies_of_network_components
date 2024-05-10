package pl.tks.gr3.cinema.viewrest.model.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketSelfInputDTO {

    private String movieTime;
    private UUID movieID;
}
