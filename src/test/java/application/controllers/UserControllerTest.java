package application.controllers;


import application.models.User;
import application.services.AccountService;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Map;
import java.util.TreeMap;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    AccountService accountService;

    @Autowired
    MockMvc mock;

    private String toJSON(Map<String, Object> data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    private Map<String, Object> addProperty(Map<String, Object> map,
                                            String propName,
                                            Object value) {
        if (value != null) {
            map.put(propName, value);
        }
        return map;
    }

    private Map<String, Object> makeUser(String login,
                                         String email,
                                         String pass,
                                         String name,
                                         String avatar) {


        Map<String, Object> data = new TreeMap<>();

        data = addProperty(data, User.Fields.LOGIN, login);
        data = addProperty(data, User.Fields.EMAIL, email);
        data = addProperty(data, User.Fields.PASSWORD, pass);
        data = addProperty(data, User.Fields.NAME, name);
        data = addProperty(data, User.Fields.AVATAR, avatar);

        return data;
    }


    @BeforeAll
    public void info() {
        LOGGER.info("accountService: " + accountService);
        LOGGER.info("mock: " + mock);
        LOGGER.info("start User Service Tests");
    }


    @Test
    public void gsonForJSON()  {

        Map<String, Object> data = new TreeMap<>();


        data.put("int_value", 42);
        data.put("str_value", "Better call Soul!");

        Assert.assertEquals("Elementary heterogeneous map",
                "{\"int_value\":42,\"str_value\":\"Better call Soul!\"}",
                toJSON(data));

        data.clear();
        String[] strArray = {"line1", "line_2", "line 3", "line \""};
        data.put("str_list", strArray);
        Object[] objArray = {42L, "str", -54.3, "sep line"};
        data.put("getero_list", objArray);


        Assert.assertEquals("Heterogeneous map with arrays, gson() -> |" + toJSON(data) + "|",
                toJSON(data),
                "{\"getero_list\":[42,\"str\",-54.3,\"sep line\"]," +
                "\"str_list\":[\"line1\",\"line_2\",\"line 3\",\"line \\\"\"]}");
    }

    @Test
    public void successfulySignup() throws Exception {
        LOGGER.info("successfulySignup ----------------------------------------------");

        final String userStringRequest1 = toJSON(makeUser("superlogin", "a@java.ru",
                "1234", null, null));
        final String userStringRequest2 = toJSON(makeUser("alex_kuz", "ad.kuznetsov.b3@cpp.ru",
                "145", "Alexander Kuzyakin", "SUPERAVAINBASE64CODE"));

        LOGGER.info("userStringRequest1 = " + userStringRequest1);
        LOGGER.info("userStringRequest2 = " + userStringRequest2);

        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userStringRequest1)
        ).andExpect(status().is2xxSuccessful());

        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userStringRequest2)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void conflictSignup() throws Exception {

        successfulySignup();

        LOGGER.info("conflictSignup ----------------------------------------------");

        final String conflictUserStringRequest1 = toJSON(makeUser("email_conflict", "a@java.ru",
                "1234", null, null));
        final String conflictUserStringRequest2 = toJSON(makeUser("alex_kuz", "login_conflict@Mail.ru",
                "145", "Alexander Kuzyakin", "SUPERAVATARINBASE64CODE"));


        LOGGER.info("conflictUserStringRequest1 = " + conflictUserStringRequest1);
        LOGGER.info("conflictUserStringRequest2 = " + conflictUserStringRequest2);


        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictUserStringRequest1)
        ).andExpect(status().isConflict());

        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictUserStringRequest2)
        ).andExpect(status().isConflict());
    }

    /*
    @Test
    public void whoami() throws Exception {

        successfulySignup();

        mock.perform(
                post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("email_conflict", "a@java.ru", "1234", null, null)))
        ).andExpect(status().is(HttpStatus.CONFLICT));

        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("alex_kuz", "login_conflict@Mail.ru",
                                "145", "Alexander Kuzyakin", "SUPERAVATARINBASE64CODE")))
        ).andExpect(status().isConflict());
    }
    */

    @After
    public void clear() {
        accountService.deleteAllUsers();
    }


}
