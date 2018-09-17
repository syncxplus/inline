package com.testbird.inline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class RuleControllerTest {
    private final static ObjectMapper MAPPER = new ObjectMapper();
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

    @Test
    public void addRule() throws Exception {
        String response = mvc.perform(post("/rule").with(httpBasic(username, password))
                .param("rate", "1024")
                .param("port", "1024")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Map map = MAPPER.readValue(response, Map.class);
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(map));
        Assert.assertEquals(map.get("status"), true);
    }
}
