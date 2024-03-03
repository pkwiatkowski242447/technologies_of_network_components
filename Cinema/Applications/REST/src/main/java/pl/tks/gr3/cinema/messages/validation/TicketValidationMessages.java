package pl.tks.gr3.cinema.messages.validation;

public class TicketValidationMessages {

    public static final String NULL_IDENTIFIER = "Ticket identifier cannot be null.";

    public static final String INVALID_TICKET_FINAL_PRICE = "Ticket final price is outside of scope of accepted values.";

    public static final String NULL_CLIENT_REFERENCE = "Ticket object could not be created for null client.";
    public static final String NULL_MOVIE_REFERENCE = "Ticket object could not be created for null movie.";

    public static final String TICKET_TYPE_NULL = "Given ticket type is a reference to null.";
}
