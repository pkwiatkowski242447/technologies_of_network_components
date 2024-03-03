package pl.tks.gr3.cinema.adapters.consts.model;

public class UserConstants {

    // Data

    public static final int LOGIN_MIN_LENGTH = 8;
    public static final int LOGIN_MAX_LENGTH = 20;

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 200;

    // Bson ids

    public static final String GENERAL_IDENTIFIER = "_id";
    public static final String USER_ID = "user_id";
    public static final String USER_LOGIN = "user_login";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_STATUS_ACTIVE = "user_status_active";
    public static final String USER_ROLE = "user_role";

    public static final String USER_DISCRIMINATOR_NAME = "_clazz";
    public static final String CLIENT_DISCRIMINATOR = "client";
    public static final String ADMIN_DISCRIMINATOR = "admin";
    public static final String STAFF_DISCRIMINATOR = "staff";
}
