package com.testbird.inline.controller;

import com.testbird.inline.metrics.UserCountryCounter;
import com.testbird.inline.metrics.UserCreateCounter;
import com.testbird.inline.metrics.UserDeleteCounter;
import com.testbird.inline.metrics.UserGauge;
import com.testbird.inline.util.OutlineApi;
import com.testbird.inline.util.TrafficRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/outline")
public class OutlineWrapper {
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;
    private final TrafficRule trafficRule;
    @Autowired
    private UserGauge userGauge;
    @Autowired
    private UserCreateCounter userCreateCounter;
    @Autowired
    private UserDeleteCounter userDeleteCounter;
    @Autowired
    private UserCountryCounter userCountryCounter;

    public OutlineWrapper(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate, @Autowired TrafficRule trafficRule) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
        this.trafficRule = trafficRule;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private Object listUsers() {
        Map map = sslTemplate.getForObject(outlineApi.listUsers(), Map.class);
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private Object createUser(HttpServletRequest request) {
        userCreateCounter.get().inc();
        userCountryCounter.get().labels(String.valueOf(request.getParameter("location"))).inc();
        Map map = sslTemplate.postForObject(outlineApi.createUser(), null, Map.class);
        userGauge.get().inc();
        return ApiResponse.successfulResponse().setData(map).generate();
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/rate/{rate}", method = RequestMethod.POST)
    private Object createUserWithRate(HttpServletRequest request, @PathVariable("rate") String rate) {
        userCreateCounter.get().inc();
        userCountryCounter.get().labels(String.valueOf(request.getParameter("location"))).inc();
        Map map = sslTemplate.postForObject(outlineApi.createUser(), null, Map.class);
        userGauge.get().inc();
        String port = String.valueOf(map.get("port"));
        map.put("add_tc_filter", trafficRule.addTcFilter(Integer.valueOf(port), Integer.valueOf(rate)));
        map.put("add_iptables_rule", trafficRule.addIptablesRule(Integer.valueOf(port)));
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @RequestMapping(value = "/{id}/name", method = RequestMethod.PUT)
    private Object updateUserName(@PathVariable("id") String id, @RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("name", name);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = sslTemplate.exchange(outlineApi.updateUserName(id), HttpMethod.PUT, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ApiResponse.successfulResponse().generate();
        } else {
            return ApiResponse.failedResponse(response.getStatusCode().name()).generate();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    private Object deleteUser(@PathVariable("id") String id) {
        userDeleteCounter.get().inc();
        ResponseEntity<String> response = sslTemplate.exchange(outlineApi.deleteUser(id), HttpMethod.DELETE, null, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            userGauge.get().dec();
            return ApiResponse.successfulResponse().generate();
        } else {
            return ApiResponse.failedResponse(response.getStatusCode().name()).generate();
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}/port/{port}/rate/{rate}", method = RequestMethod.DELETE)
    private Object deleteUser(@PathVariable("id") String id, @PathVariable("port") String port, @PathVariable("rate") String rate) {
        userDeleteCounter.get().inc();
        ResponseEntity<String> response = sslTemplate.exchange(outlineApi.deleteUser(id), HttpMethod.DELETE, null, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            userGauge.get().dec();
            Map<String, Object> map = new HashMap<>();
            map.put("rm_iptables_rule", trafficRule.rmIptablesRule(Integer.valueOf(port)));
            map.put("rm_tc_filter", trafficRule.rmTcFilter(Integer.valueOf(port), Integer.valueOf(rate)));
            return ApiResponse.successfulResponse().setData(map).generate();
        } else {
            return ApiResponse.failedResponse(response.getStatusCode().name()).generate();
        }
    }

    @Deprecated
    @RequestMapping(value = "/{id}/stats", method = RequestMethod.GET)
    private Object userStats(@PathVariable("id") String id) {
        Map<String, Object> stats = new HashMap<String, Object>() {{
            put("id", id);
            put("stats", 0);
        }};
        Map map = sslTemplate.getForObject(outlineApi.userStats(), Map.class);
        if (map != null && map.containsKey("bytesTransferredByUserId")) {
            Map metrics = (Map) map.get("bytesTransferredByUserId");
            if (metrics != null && metrics.containsKey(id)) {
                stats.put("stats", metrics.get(id));
                return ApiResponse.successfulResponse().setData(stats).generate();
            }
        }
        return ApiResponse.successfulResponse().setData(stats).generate();
    }

    @ExceptionHandler
    private Object exceptionHandler(IOException e) {
        return ApiResponse.failedResponse(e.getMessage()).setException(e.getClass().getName()).generate();
    }
}
