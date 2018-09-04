package com.testbird.inline.controller;

import com.testbird.inline.util.OutlineApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/outline")
public class OutlineWrapper {
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;

    public OutlineWrapper(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private Object listUsers() {
        return sslTemplate.getForObject(outlineApi.listUsers(), Map.class);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private Object createUser() {
        return sslTemplate.postForObject(outlineApi.createUser(), null, String.class);
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    private Object updateUserName(@PathVariable("id") String id, @RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("name", name);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Object> response = sslTemplate.exchange(outlineApi.updateUserName(id), HttpMethod.PUT, entity, Object.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    private Object deleteUser(@PathVariable("id") String id) {
        ResponseEntity<Object> response = sslTemplate.exchange(outlineApi.deleteUser(id), HttpMethod.DELETE, null, Object.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}
