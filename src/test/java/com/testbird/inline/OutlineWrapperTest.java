package com.testbird.inline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OutlineWrapperTest {
    private final static String CONTEXT = "/outline";
    private final static String USERNAME = "inline";
    private static String userId;

    @Autowired
    private MockMvc mvc;

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
        String result = mvc.perform(post(CONTEXT))
                .andExpect(status().is2xxSuccessful())
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
                .param("name", USERNAME)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void t3DeleteUser() throws Exception {
        mvc.perform(delete(CONTEXT + "/" + userId)).andExpect(status().is2xxSuccessful());
    }

    /**
     * {
     * "accessKeys" : [{"id", "name", "password", "port", "method", "accessUrl"}, ... ],
     * "users" : [ ]
     * }
     */
    @After
    public void listUsers() throws Exception {
        String result = mvc.perform(get(CONTEXT))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(result, Map.class);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
    }
}
