package pl.tks.gr3.cinema.domain_model.validation;

public class MovieValidationMessages {
    public final static String NULL_IDENTIFIER = "No movie can have an identifier that is null.";

    public final static String NULL_MOVIE_TITLE = "Movie title cannot be null.";
    public final static String MOVIE_TITLE_TOO_SHORT = "No movie title can be shorter than one character.";
    public final static String MOVIE_TITLE_TOO_LONG = "No movie title can be shorter than one character.";

    public final static String MOVIE_BASE_PRICE_TOO_LOW = "Movie base price cannot be negative.";
    public final static String MOVIE_BASE_PRICE_TOO_HIGH = "Movie base price cannot be this high.";

    public final static String SCREENING_ROOM_NUMBER_TOO_LOW = "There is no screening room with number that low in this cinema.";
    public final static String SCREENING_ROOM_NUMBER_TOO_HIGH = "There is no screening room with that number that high in this cinema.";

    public final static String NUMBER_OF_AVAILABLE_SEATS_NEGATIVE = "Number of available seats could not be a negative value.";
    public final static String NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT = "Number of available seats could not pass upper limit of chairs in any screening room.";
}
