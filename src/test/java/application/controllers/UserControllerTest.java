package application.controllers;


import application.models.User;
import application.services.AccountService;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Map;
import java.util.TreeMap;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest {

    private class APIUrls {
        private static final String API_PATH = "/api";

        private static final String SIGNUP = API_PATH + "/signup";
        private static final String SIGNIN = API_PATH + "/signin";
        private static final String LOGOUT = API_PATH + "/signout";

        private static final String LEADERBOARD = API_PATH + "/leaderboard";

        public static final String ISAUTH = API_PATH + "/isauthorized";

        private static final String USER = API_PATH + "/user";
        private static final String USER_UPDATE = USER + "/update";

    }


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

    private Map<String, Object> makeUser(String login, String email, String pass,
                                         String name, String avatar) {

        Map<String, Object> data = new TreeMap<>();

        data = addProperty(data, User.Fields.LOGIN, login);
        data = addProperty(data, User.Fields.EMAIL, email);
        data = addProperty(data, User.Fields.PASSWORD, pass);
        data = addProperty(data, User.Fields.NAME, name);
        data = addProperty(data, User.Fields.AVATAR, avatar);

        return data;
    }

    
    @Test
    public void gsonForJSON()  {

        Map<String, Object> data = new TreeMap<>();

        data.put("int_value", 42);
        data.put("str_value", "Better call Soul!");

        Assert.assertTrue(
                "Elementary heterogeneous map",
                toJSON(data)
                        .equals("{\"int_value\":42,\"str_value\":\"Better call Soul!\"}")
        );

        data.clear();
        String[] strArray = {"line1", "line_2", "line 3", "line \""};
        data.put("str_list", strArray);
        Object[] objArray = {42L, "str", -54.3, "sep line"};
        data.put("getero_list", objArray);

        System.out.println();

        Assert.assertTrue(
                "Heterogeneous map with arrays, gson() -> |" + toJSON(data) + "|",
                toJSON(data)
                        .equals("{\"getero_list\":[42,\"str\",-54.3,\"sep line\"]," +
                                "\"str_list\":[\"line1\",\"line_2\",\"line 3\",\"line \\\"\"]}")

        );
    }

    @Test
    public void successfulySignup() throws Exception {

        mock.perform(
                post(APIUrls.SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("superlogin", "a@java.ru", "1234", null, null)))
        ).andExpect(status().is2xxSuccessful());

        mock.perform(
                post(APIUrls.SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("alex_kuz", "ad.kuznetsov.b3@cpp.ru",
                                "145", "Alexander Kuzyakin", "SUPERAVAINBASE64CODE")))
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void conflictSignup() throws Exception {

        successfulySignup();

        mock.perform(
                get(APIUrls.SIGNUP)
        ).andExpect(status().isConflict());
    }


    /*
    @Test
    public void whoami() throws Exception {
        successfulySignup();

        mock.perform(
                post(APIUrls.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("email_conflict", "a@java.ru", "1234", null, null)))
        ).andExpect(status().is2xxSuccessful());
    }
    */

    @After
    public void clear() {
        accountService.deleteAllUsers();
    }


}
