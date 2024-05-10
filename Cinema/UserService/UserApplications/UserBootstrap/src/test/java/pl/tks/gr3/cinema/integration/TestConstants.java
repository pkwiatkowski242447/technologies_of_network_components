package pl.tks.gr3.cinema.integration;

public class TestConstants {


    // Base URL

    public static final String baseURL = "https://localhost:8080/api/v1";

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
}
