package pl.tks.gr3.cinema.adapters.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.tks.gr3.cinema.adapters.consts.model.TicketConstants;
import pl.tks.gr3.cinema.adapters.messages.validation.TicketValidationMessages;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketEnt {

    @BsonProperty(TicketConstants.GENERAL_IDENTIFIER)
    @NotNull(message = TicketValidationMessages.NULL_IDENTIFIER)
    @Setter(AccessLevel.NONE)
    private UUID ticketID;

    @BsonProperty(TicketConstants.MOVIE_TIME)
    private LocalDateTime movieTime;

    @BsonProperty(TicketConstants.TICKET_FINAL_PRICE)
    @PositiveOrZero(message = TicketValidationMessages.INVALID_TICKET_FINAL_PRICE)
    @Setter(AccessLevel.NONE)
    private double ticketPrice;

    @BsonProperty(TicketConstants.USER_ID)
    @NotNull(message = TicketValidationMessages.NULL_CLIENT_REFERENCE)
    @Setter(AccessLevel.NONE)
    private UUID userID;

    @BsonProperty(TicketConstants.MOVIE_ID)
    @NotNull(message = TicketValidationMessages.NULL_MOVIE_REFERENCE)
    @Setter(AccessLevel.NONE)
    private UUID movieID;

    // Constructors

    @BsonCreator
    public TicketEnt(@BsonProperty(TicketConstants.GENERAL_IDENTIFIER) UUID ticketID,
                     @BsonProperty(TicketConstants.MOVIE_TIME) LocalDateTime movieTime,
                     @BsonProperty(TicketConstants.TICKET_FINAL_PRICE) double ticketPrice,
                     @BsonProperty(TicketConstants.USER_ID) UUID userID,
                     @BsonProperty(TicketConstants.MOVIE_ID) UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketPrice = ticketPrice;
        this.userID = userID;
        this.movieID = movieID;
    }

    // Other methods

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TicketEnt ticket = (TicketEnt) o;

        return new EqualsBuilder()
                .append(ticketID, ticket.ticketID)
                .append(movieTime, ticket.movieTime)
                .append(ticketPrice, ticket.ticketPrice)
                .append(userID, ticket.userID)
                .append(movieID, ticket.movieID)
                .isEquals();
    }

    // HashCode

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(ticketID)
                .append(movieTime)
                .append(ticketPrice)
                .append(userID)
                .append(movieID)
                .toHashCode();
    }

    // ToString method

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Ticket ID: ", ticketID)
                .append("Movie time: ", movieTime)
                .append("Ticket final price: ", ticketPrice)
                .append("Client: ", userID.toString())
                .append("Movie: ", movieID.toString())
                .toString();
    }
}
