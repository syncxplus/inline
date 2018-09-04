package com.testbird.inline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OutlineWrapperTest {
    private final static String CONTEXT = "/outline";
    private final static String TEST_NAME = "inline";
    private static String userId;

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @Value("${spring.security.user.name:user}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Before
    public void setup() {
        mvc =  MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * {
     * "id" : "",
     * "name" : "",
     * "password" : "",
     * "port" : 0,
     * "method" : "",
     * "accessUrl" : ""
     * }
     */
    @Test
    public void t1CreateUser() throws Exception {
        String result = mvc.perform(post(CONTEXT).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(result, Map.class);
        userId = String.valueOf(map.get("id"));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
    }

    @Test
    public void t2UpdateUserName() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(CONTEXT + "/" + userId + "/name")
                .with(httpBasic(username, password))
                .param("name", TEST_NAME)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    public void t3DeleteUser() throws Exception {
        mvc.perform(delete(CONTEXT + "/" + userId).with(httpBasic(username, password)))
                .andExpect(status().isOk());
    }

    /**
     * {
     * "accessKeys" : [{"id", "name", "password", "port", "method", "accessUrl"}, ... ],
     * "users" : [ ]
     * }
     */
    @After
    public void listUsers() throws Exception {
        String result = mvc.perform(get(CONTEXT).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(result, Map.class);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
    }
}
