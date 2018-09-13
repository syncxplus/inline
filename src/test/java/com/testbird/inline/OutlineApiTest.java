package com.testbird.inline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbird.inline.util.OutlineApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class OutlineApiTest {
    @Autowired
    OutlineApi outlineApi;
    @Autowired
    RestTemplate sslTemplate;

    @Test
    public void listUsers() throws JsonProcessingException {
        ResponseEntity<Map> response = sslTemplate.getForEntity(outlineApi.listUsers(), Map.class);
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody()));
    }

    /**
     * {
     *   "bytesTransferredByUserId" : {
     *     "0" : 942672017,
     *     "1" : 615353875,
     *     "3" : 47,
     *     "6" : 47,
     *     "7" : 47
     *   }
     * }
     */
    @Test
    public void userStats () throws JsonProcessingException {
        ResponseEntity<Map> response = sslTemplate.getForEntity(outlineApi.userStats(), Map.class);
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody()));
    }
}
