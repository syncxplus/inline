package com.testbird.inline.controller;

import com.testbird.inline.metrics.UserCountryCounter;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/outline")
public class OutlineWrapper {
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;
    private final TrafficRule trafficRule;
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
        userCountryCounter.get().labels(String.valueOf(request.getParameter("location"))).inc();
        Map map = sslTemplate.postForObject(outlineApi.createUser(), null, Map.class);
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.DELETE)
    private Object deleteUsers(@RequestBody(required = false) List<Map<String, Integer>> accounts) {
        List<Integer> accessKeys = new ArrayList<>();
        accounts.forEach(account -> {
            Integer id = account.get("id");
            if (id != null) {
                accessKeys.add(id);
            }
            Integer port = account.get("port");
            Integer rate = account.get(("rate"));
            if (port != null) {
                trafficRule.rmIptablesRule(port);
                if (rate != null) {
                    trafficRule.rmTcFilter(port, rate);
                }
            }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, List> params= new HashMap<>();
        params.put("accessKeys", accessKeys);
        HttpEntity<Map<String, List>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = sslTemplate.exchange(outlineApi.deleteUsers(), HttpMethod.DELETE, entity, Map.class);
        return ApiResponse.successfulResponse().setData(response.getBody()).generate();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"/rate/{rate}", "/rate/{rate}/count/{count}"}, method = RequestMethod.POST)
    private Object createUserWithRate(HttpServletRequest request, @PathVariable String rate, @PathVariable(required = false) String count) {
        userCountryCounter.get().labels(String.valueOf(request.getParameter("location"))).inc();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("rate", rate);
        String api;
        if (count != null) {
            int c = 1;
            try {
                c = Integer.parseInt(count);
            } catch (Exception e) {
                // ignored
            }
            api = outlineApi.createMultiUser(c);
        } else {
            api = outlineApi.createUser();
        }
        Object o = sslTemplate.postForObject(api, new HttpEntity<>(params, headers), Object.class);
        Map map;
        if (o instanceof Map) {
            map = (Map) o;
            String port = String.valueOf(map.get("port"));
            trafficRule.addTcFilter(Integer.valueOf(port), Integer.valueOf(rate));
            trafficRule.addIptablesRule(Integer.valueOf(port));
        } else {
            List<Map> list = (List<Map>) o;
            list.forEach(m -> {
                String port = String.valueOf(m.get("port"));
                trafficRule.addTcFilter(Integer.valueOf(port), Integer.valueOf(rate));
                trafficRule.addIptablesRule(Integer.valueOf(port));
            });
            map = new HashMap();
            map.put("accounts", o);
        }
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
        ResponseEntity<String> response = sslTemplate.exchange(outlineApi.deleteUser(id), HttpMethod.DELETE, null, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ApiResponse.successfulResponse().generate();
        } else {
            return ApiResponse.failedResponse(response.getStatusCode().name()).generate();
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}/port/{port}/rate/{rate}", method = RequestMethod.DELETE)
    private Object deleteUser(@PathVariable("id") String id, @PathVariable("port") String port, @PathVariable("rate") String rate) {
        ResponseEntity<String> response = sslTemplate.exchange(outlineApi.deleteUser(id), HttpMethod.DELETE, null, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> map = new HashMap<>();
            map.put("rm_iptables_rule", trafficRule.rmIptablesRule(Integer.valueOf(port)));
            map.put("rm_tc_filter", trafficRule.rmTcFilter(Integer.valueOf(port), Integer.valueOf(rate)));
            return ApiResponse.successfulResponse().setData(map).generate();
        } else {
            return ApiResponse.failedResponse(response.getStatusCode().name()).generate();
        }
    }

    /**
     * @reset: This is a debug API
     */
    @RequestMapping("/reset")
    private void reset() {
        String path = "/root/shadowbox/persisted-state/shadowbox_config.json";
        String init = "{\"accessKeys\":[{\"id\":\"0\",\"metricsId\":\"7f6f5012-6dcb-469f-8428-bdafa9f4ee6b\",\"name\":\"\",\"port\":1024,\"encryptionMethod\":\"chacha20-ietf-poly1305\",\"password\":\"shadowbox123\"}],\"nextId\":1}";
        try {
            Files.write(Paths.get(path), init.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            Runtime.getRuntime().exec("sudo docker restart shadowbox");
        } catch (IOException e) {
            e.printStackTrace();
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
