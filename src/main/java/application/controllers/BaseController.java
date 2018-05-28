package application.controllers;

import org.springframework.http.MediaType;


public class BaseController {
    protected static final String JSON = MediaType.APPLICATION_JSON_UTF8_VALUE; // "application/json;charset=UTF-8"

    public static final String USER_ID = "Deadlinez_user_id";
    static final String API_PATH = "/api";

    static final String LOG_TAB_1 = "  ";

}
