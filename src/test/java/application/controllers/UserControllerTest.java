package application.controllers;


import application.models.User;
import application.services.AccountService;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Map;
import java.util.TreeMap;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest {

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


    @Autowired
    AccountService accountService;

    @Autowired
    MockMvc mock;


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
    public void signup() throws Exception {
        mock.perform(
                post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(makeUser("superlogin", "a@java.ru", "1234", null, null)))
        ).andExpect(status().is2xxSuccessful());
    }

    @After
    public void clear() {
        accountService.deleteAllUsers();
    }


}
