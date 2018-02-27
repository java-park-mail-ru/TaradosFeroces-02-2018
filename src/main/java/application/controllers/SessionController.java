package application.controllers;


import application.utils.requests.UserSignInRequest;
import application.utils.responses.Message;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@CrossOrigin
@RequestMapping("/alexalone")
public class SessionController {
    private static final String JSON = MediaType.APPLICATION_JSON_UTF8_VALUE; // "application/json;charset=UTF-8"

    @PostMapping(path = "/signup", consumes = JSON, produces = JSON)
    public Message signup(@RequestBody UserSignInRequest body, HttpSession httpSession) {
        return new Message("Created user " + body.getLogin());
    }
}
