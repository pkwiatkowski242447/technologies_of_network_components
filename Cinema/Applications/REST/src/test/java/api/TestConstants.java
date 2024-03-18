package api;

public class TestConstants {

    // Database name

    public final static String databaseName = "default";

    // Base URL

    public static final String baseURL = "https://localhost:8000/api/v1";

    // Auth endpoints

    public static final String authURL = baseURL + "/auth";

    // Register endpoints

    public static final String registerURL = authURL + "/register";
    public static final String clientRegisterURL = registerURL + "/client";
    public static final String staffRegisterURL = registerURL + "/staff";
    public static final String adminRegisterURL = registerURL + "/admin";

    // Login endpoints

    public static final String loginURL = authURL + "/login";
    public static final String clientLoginURL = loginURL + "/client";
    public static final String staffLoginURL = loginURL + "/staff";
    public static final String adminLoginURL = loginURL + "/admin";

    // Users

    public static final String clientsURL = baseURL + "/clients";
    public static final String staffsURL = baseURL + "/staffs";
    public static final String adminsURL = baseURL + "/admins";

    // Movies

    public static final String moviesURL = baseURL + "/movies";

    // Tickets

    public static final String ticketsURL = baseURL + "/tickets";
}
