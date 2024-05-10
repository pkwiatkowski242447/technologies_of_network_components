package pl.tks.gr3.cinema.viewsoap.consts;

public class WebServiceConsts {

    public static final String GENERAL_NAMESPACE = "http://viewsoap.adapters.cinema";
    public static final String USERS_NAMESPACE = GENERAL_NAMESPACE + "/users";

    public static final String LOGIN_NAMESPACE = USERS_NAMESPACE + "/login";
    public static final String REGISTER_NAMESPACE = USERS_NAMESPACE + "/register";

    public static final String CLIENT_LOGIN_INPUT = "clientLoginRequest";
    public static final String CLIENT_LOGIN_OUTPUT = "clientLoginResponse";

    public static final String STAFF_LOGIN_INPUT = "staffLoginRequest";
    public static final String STAFF_LOGIN_OUTPUT = "staffLoginResponse";

    public static final String ADMIN_LOGIN_INPUT = "adminLoginRequest";
    public static final String ADMIN_LOGIN_OUTPUT = "adminLoginResponse";

    public static final String CLIENT_REGISTER_INPUT = "clientRegisterRequest";
    public static final String CLIENT_REGISTER_OUTPUT = "clientRegisterResponse";

    public static final String STAFF_REGISTER_INPUT = "staffRegisterRequest";
    public static final String STAFF_REGISTER_OUTPUT = "staffRegisterResponse";

    public static final String ADMIN_REGISTER_INPUT = "adminRegisterRequest";
    public static final String ADMIN_REGISTER_OUTPUT = "adminRegisterResponse";

    public static final String USER_REGISTER_INPUT = "userRegisterRequest";
    public static final String USER_REGISTER_OUTPUT = "userRegisterResponse";
    public static final String USER_OUTPUT_ELEMENT = "userOutputElement";
}
