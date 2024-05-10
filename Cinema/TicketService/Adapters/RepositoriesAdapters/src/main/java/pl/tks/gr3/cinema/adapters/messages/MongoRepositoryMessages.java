package pl.tks.gr3.cinema.adapters.messages;

public class MongoRepositoryMessages {

    public static final String USER_DOC_OBJECT_NOT_FOUND = "User document with given login could not be found in the database.";
    public static final String CLIENT_DOC_OBJECT_NOT_FOUND = "Client document with given ID could not be found in the database.";
    public static final String ADMIN_DOC_OBJECT_NOT_FOUND = "Admin document with given ID could not be found in the database.";
    public static final String STAFF_DOC_OBJECT_NOT_FOUND = "Staff document with given ID could not be found in the database.";
    public static final String MOVIE_DOC_OBJECT_NOT_FOUND = "Movie document with given ID could not be found in the database.";
    public static final String TICKET_DOC_OBJECT_NOT_FOUND = "Ticket document with given ID could not be found in the database.";
    public static final String DOC_OBJECT_NOT_FOUND = "Document object with given ID could not be found in the database.";

    public static final String ID_REFERENCE_TO_DOCUMENT_OF_DIFFERENT_TYPE = "Given ID references document of different type.";

    public static final String USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND = "User document for given user object could not be found in the database.";
    public static final String CLIENT_DOC_FOR_CLIENT_OBJ_NOT_FOUND = "Client document for given client object could not be found in the database.";
    public static final String ADMIN_DOC_FOR_ADMIN_OBJ_NOT_FOUND = "Admin document for given admin object could not be found in the database.";
    public static final String STAFF_DOC_FOR_STAFF_OBJ_NOT_FOUND = "Staff document for given staff object could not be found in the database.";

    public static final String MOVIE_HAS_UNFINISHED_ALLOCATIONS = "There are tickets that contain reference to this movie.";

    public static final String ALLOCATION_NOT_POSSIBLE_SINCE_CLIENT_INACTIVE = "Client object is inactive, and therefore could not create ticket allocation.";
    public static final String TICKET_DOC_FOR_TICKET_OBJ_NOT_FOUND = "Ticket document for given ticket object could not be found in the database.";

    public static final String USER_TYPE_NOT_FOUND = "User with given user type could not be found.";
}
