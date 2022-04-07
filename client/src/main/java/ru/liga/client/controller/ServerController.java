package ru.liga.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.liga.client.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class ServerController {
    private final RestTemplate restTemplate;
    private final String urlResource;

    public ServerController(String urlResource) {
        this.urlResource = urlResource;
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(1))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
        this.restTemplate.getMessageConverters().add(0,
                new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void saveNewUser(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(user.toJson(), httpHeaders);
        log.info("Сохранение пользователя {}", user.getId());
        ResponseEntity<HttpStatus> response = restTemplate.exchange(urlResource, HttpMethod.POST, entity, HttpStatus.class);
    }

    public User getUserById(long id) {
        log.info("get user id = {}", id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        String url = urlResource + "/" + id;
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
            user = response.getBody();


        } catch (RestClientException e) {
            log.info("Данных о пользователе с id {} нет", id);
        }
        if (user != null) {
            fillLoversMap(id, user);
            fillLovedMap(id, user);
        }

        return user;
    }

    public void fillLovedMap(long id, User user) {
        log.info("fillLovedMap userid {} ", id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        try {
            String urlLoved = urlResource + "/loved/" + id;
            HashMap<Long, User> lovedMap = user.getLoved();
            ResponseEntity<User[]> responseLovers = restTemplate.exchange(urlLoved, HttpMethod.GET, entity, User[].class);
            for (User u : Objects.requireNonNull(responseLovers.getBody())) {
                lovedMap.put(u.getId(), u);
            }
        } catch (RestClientException e) {
            log.info("Данных о возлюбленных нет user {}", id);
        }
    }

    public void fillLoversMap(long id, User user) {
        log.info("fillLoversMap userid {} ", id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        try {
            String urlLovers = urlResource + "/lovers/" + id;
            HashMap<Long, User> loversMap = user.getLovers();
            ResponseEntity<User[]> responseLovers = restTemplate.exchange(urlLovers, HttpMethod.GET, entity, User[].class);
            for (User u : Objects.requireNonNull(responseLovers.getBody())) {
                loversMap.put(u.getId(), u);
            }
        } catch (RestClientException e) {
            log.info("Данных о влюбленных нет user {}", id);
        }
    }

    public Map<Long, User> getAllWithFilter(Predicate<? super User> filter) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        User[] users = null;
        Map<Long, User> usersMap = new HashMap<>();
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(urlResource
                    , HttpMethod.GET, entity, User[].class);
            users = response.getBody();
            if (users != null) {
                usersMap = Arrays.stream(response.getBody()).filter(filter)
                        .collect(Collectors.toMap(User::getId, user -> user));
            }
            log.info("getAllwithfilter\n{}", usersMap);
        } catch (RestClientException e) {
            log.info("Данных о пользователях нет (поисковый запрос с фильтром {})", filter);
        }
        return usersMap;

    }

    public void removeLover(long userId, Long lastProfile) {
        log.info("Удаление Возлюбленного {} юзера {} из бд", lastProfile, userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.delete(urlResource + "/lovers/" + userId + "/" + lastProfile);
        } catch (RestClientException e) {
            log.info("Возлюбленный {} юзера {} не удален из бд", lastProfile, userId);
        }
    }

    public void addNewLover(long userId, Long lastProfile) {
        log.info("Добавление Возлюбленного {} юзера {} в бд", lastProfile, userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);

        String url = urlResource + "/lovers/" + userId + "/" + lastProfile;
        try {
            restTemplate.exchange(url, HttpMethod.POST,
                    entity, HttpStatus.class);
        } catch (RestClientException e) {
            log.info("Возлюбленный {} юзера {} не добавлен в бд", userId, lastProfile);
        }
    }
}
