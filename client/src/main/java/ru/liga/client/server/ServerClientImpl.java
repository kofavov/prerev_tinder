package ru.liga.client.server;

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
public class ServerClientImpl implements ServerClient {
    private final RestTemplate restTemplate;
    private final String urlResource;
    private final String loversResource;
    private final String lovedResource;

    public ServerClientImpl(String urlResource, String loversResource, String lovedResource) {
        this.urlResource = urlResource;
        this.loversResource = loversResource;
        this.lovedResource = lovedResource;

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(1))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
        this.restTemplate.getMessageConverters().add(0,
                new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public void saveNewUser(User user) {
        HttpEntity<String> entity = getHttpEntity(user);
        log.info("Сохранение пользователя {}", user.getId());
        ResponseEntity<HttpStatus> response = restTemplate.exchange(urlResource, HttpMethod.POST, entity, HttpStatus.class);
    }

    @Override
    public User getUserById(long id) {
        log.info("get user id = {}", id);
        HttpEntity<String> entity = getHttpEntity();
        String url = urlResource + "/" + id;
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
            user = response.getBody();
        } catch (RestClientException e) {
            log.error("Данных о пользователе с id {} нет", id,e);
        }
        if (user != null) {
            fillLoversMap(id, user);
            fillLovedMap(id, user);
        }

        return user;
    }

    @Override
    public void fillLovedMap(long id, User user) {
        log.info("fillLovedMap userid {} ", id);
        HttpEntity<String> entity = getHttpEntity();
        try {
            HashMap<Long, User> lovedMap = user.getLoved();
            ResponseEntity<User[]> responseLovers = getResponseEntity(id, entity, lovedResource);
            fillMap(lovedMap,responseLovers);
        } catch (RestClientException e) {
            log.error("Данных о возлюбленных нет user {}", id,e);
        }
    }

    @Override
    public void fillLoversMap(long id, User user) {
        log.info("fillLoversMap userid {} ", id);
        HttpEntity<String> entity = getHttpEntity();
        try {
            Map<Long, User> loversMap = user.getLovers();
            ResponseEntity<User[]> responseLovers = getResponseEntity(id, entity, loversResource);
            fillMap(loversMap, responseLovers);
        } catch (RestClientException e) {
            log.error("Данных о влюбленных нет user {}", id,e);
        }
    }

    @Override
    public Map<Long, User> getAllWithFilter(Predicate<? super User> filter) {
        HttpEntity<String> entity = getHttpEntity();
        User[] users;
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
            log.error("Данных о пользователях нет (запрос с фильтром)",e);
        }
        return usersMap;

    }

    @Override
    public void removeLover(long userId, Long lastProfile) {
        log.info("Удаление Возлюбленного {} юзера {} из бд", lastProfile, userId);
        HttpEntity<String> entity = getHttpEntity();
        try {
            restTemplate.delete(loversResource + "/" + userId + "/" + lastProfile);
        } catch (RestClientException e) {
            log.error("Возлюбленный {} юзера {} не удален из бд", lastProfile, userId,e);
        }
    }

    @Override
    public void addNewLover(long userId, Long lastProfile) {
        log.info("Добавление Возлюбленного {} юзера {} в бд", lastProfile, userId);
        HttpEntity<String> entity = getHttpEntity();

        String url = loversResource + "/" + userId + "/" + lastProfile;
        try {
            restTemplate.exchange(url, HttpMethod.POST,
                    entity, HttpStatus.class);
        } catch (RestClientException e) {
            log.error("Возлюбленный {} юзера {} не добавлен в бд", userId, lastProfile,e);
        }
    }

    private HttpEntity<String> getHttpEntity(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(user.toJson(), httpHeaders);
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(httpHeaders);
    }

    private ResponseEntity<User[]> getResponseEntity(long id, HttpEntity<String> entity, String urlResource) {
        String urlLoved = urlResource + "/" + id;
        return restTemplate.exchange(urlLoved, HttpMethod.GET, entity, User[].class);
    }

    private void fillMap(Map<Long, User> map, ResponseEntity<User[]> response) {
        for (User u : Objects.requireNonNull(response.getBody())) {
            map.put(u.getId(), u);
        }
    }
}
