package pl.tks.gr3.cinema.adapters.consts.model;

public class MovieConstants {

    // Data

    public static final int MOVIE_TITLE_MIN_LENGTH = 1;
    public static final int MOVIE_TITLE_MAX_LENGTH = 150;

    public static final int MOVIE_BASE_PRICE_MIN_VALUE = 0;
    public static final int MOVIE_BASE_PRICE_MAX_VALUE = 100;

    public static final int SCREENING_ROOM_NUMBER_MIN_VALUE = 1;
    public static final int SCREENING_ROOM_NUMBER_MAX_VALUE = 30;

    public static final int NUMBER_OF_AVAILABLE_SEATS_MIN_VALUE = 0;
    public static final int NUMBER_OF_AVAILABLE_SEATS_MAX_VALUE = 120;

    // Bson ids

    public static final String GENERAL_IDENTIFIER = "_id";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_BASE_PRICE = "movie_base_price";
    public static final String SCREENING_ROOM_NUMBER = "scr_room_number";
    public static final String NUMBER_OF_AVAILABLE_SEATS = "number_of_available_seats";
}
