package ru.liga.client.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.liga.client.entity.User;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainController {
    private final RestTemplate restTemplate;

    public MainController() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(1))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
    }


    public String getAllUsers(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String urlResource = "http://localhost:8080/api/users";
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<User[]> response = restTemplate.exchange(urlResource, HttpMethod.GET, entity, User[].class);
        System.out.println(Arrays.toString(response.getBody()));
        List<User> users = Arrays.asList(Objects.requireNonNull(response.getBody()));
        return users.toString();
    }

}
