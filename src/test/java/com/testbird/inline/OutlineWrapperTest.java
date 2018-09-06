package com.testbird.inline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.ResultActions;
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
    private final static ObjectMapper MAPPER = new ObjectMapper();
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
        Map response = execute(mvc.perform(post(CONTEXT).with(httpBasic(username, password))));
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        Assert.assertEquals(response.get("status"), true);
        userId = String.valueOf(response.get("id"));
    }

    @Test
    public void t2UpdateUserName() throws Exception {
        Map response = execute(mvc.perform(MockMvcRequestBuilders
                .put(CONTEXT + "/" + userId + "/name")
                .with(httpBasic(username, password))
                .param("name", TEST_NAME)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ));
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        Assert.assertEquals(response.get("status"), true);
    }

    /**
     * {
     * "accessKeys" : [{"id", "name", "password", "port", "method", "accessUrl"}, ... ],
     * "users" : [ ]
     * }
     */
    @Test
    public void t3ListUsers() throws Exception {
        Map response = execute(mvc.perform(get(CONTEXT).with(httpBasic(username, password))));
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        Assert.assertEquals(response.get("status"), true);
    }

    @Test
    public void t4DeleteUser() throws Exception {
        Map response = execute(mvc.perform(delete(CONTEXT + "/" + userId).with(httpBasic(username, password))));
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        Assert.assertEquals(response.get("status"), true);
    }

    private Map execute(ResultActions result) throws Exception {
        String content = result
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return MAPPER.readValue(content, Map.class);
    }
}
