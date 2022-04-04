//package ru.liga.client.controller;
//
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.*;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.web.client.RestTemplate;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import ru.liga.client.entity.User;
//import ru.liga.client.telegram.Bot;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.*;

//public class MainController {
//    private final RestTemplate restTemplate;
//    private final String urlResource = "http://localhost:8080/api/users";
//
//
//    public MainController() {
//        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
//        this.restTemplate = restTemplateBuilder
//                .setConnectTimeout(Duration.ofSeconds(1))
//                .setReadTimeout(Duration.ofSeconds(2))
//                .build();
//        this.restTemplate.getMessageConverters().add(0,new StringHttpMessageConverter(StandardCharsets.UTF_8));
//    }
//
//    public void execute(Message message, Bot bot){
//        String commandSting = message.getText();
//        String[] command = commandSting.split(" ");
//        long id = message.getChatId();
//        System.out.println(id);
//        User user = getUser(message, bot, command);
//        executeCommand(message, bot, commandSting, command, user);
//        System.out.println(user);
//    }
//
//    private void executeCommand(Message message, Bot bot, String commandSting, String[] command, User user) {
//        switch (command[0]){
//            case "/all":
//                bot.sendMessage(message, getAllUsers().toString());
//                break;
//            case "/profile":
//                bot.sendMessage(message,Objects.requireNonNull(user).toString());
//                break;
//            case "/new": addNewUser(message, bot);break;
//            case "/name":changeName(commandSting, user);break;
//            case "/gender":changeGender(commandSting, user);break;
//            case "/head":changeHead(commandSting, user);break;
//            case "/desc":changeDescription(commandSting, user);break;
//            case "/lovers":
//                bot.sendMessage(message,user.getLovers().values().toString());
//                break;
//            case "/loved":
//                bot.sendMessage(message,user.getLoved().values().toString());
//                break;
//        }
//    }
//
//    private User getUser(Message message, Bot bot, String[] command) {
//        User user = null;
//        if (!command[0].equals("/new")) {
//            try {
//                user = getUserById(message.getChatId());
//                System.out.println(user);
//            } catch (Exception e) {
//                bot.sendMessage(message, "Создайте анкету с помощью команды /new\n" +
//                        "Затем воспользуйтесь командами\n" +
//                        "/name установить имя \n" +
//                        "/gender установить пол (Сударъ или Судаыня) \n" +
//                        "/head установить заголовок \n" +
//                        "/desc установить описание");
//            }
//        }
//        return user;
//    }
//
//    private void changeDescription(String command, User user) {
//        if (user!=null) {
//            String s = command.split(" ",2)[1];
//            user.setDescription(s);
//            changeUser(user);
//        }
//    }
//
//    private void changeHead(String command, User user) {
//        if (user!=null) {
//            String s = command.split(" ",2)[1];
//            user.setHeading(s);
//            changeUser(user);
//        }
//    }
//
//    private void changeGender(String command, User user) {
//        if (user!=null) {
//            String s = command.split(" ",2)[1];
//            user.setGender(s);
//            changeUser(user);
//        }
//    }
//
//
//
//    private void changeName(String command, User user) {
//        if (user!=null) {
//            String s = command.split(" ",2)[1];
//            user.setName(s);
//            changeUser(user);
//        }
//    }
//
//    private void changeUser(User user) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<String>(user.toString(),httpHeaders);
////        System.out.println(entity);
//        ResponseEntity<HttpStatus> response = restTemplate.exchange(urlResource, HttpMethod.PUT, entity, HttpStatus.class);
//    }
//
//    private void addNewUser(Message message,Bot bot) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        User user = getUserFromMessage(message);
//        HttpEntity<String> entity = new HttpEntity<String>(user.toString(),httpHeaders);
////        System.out.println(entity);
//        ResponseEntity<HttpStatus> response = restTemplate.exchange(urlResource, HttpMethod.POST, entity, HttpStatus.class);
//    }
//
//    private User getUserFromMessage(Message message) {
//        return new User(message.getChatId(),"","Male","","");
//    }
//
//    public List<User> getAllUsers(){
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
//        ResponseEntity<User[]> response = restTemplate.exchange(urlResource, HttpMethod.GET, entity, User[].class);
//
//        List<User> users = Arrays.asList(Objects.requireNonNull(response.getBody()));
//        return users;
//    }
//
//    public User getUserById(long id){
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
//        String url = urlResource + "/" + id;
//        ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
//        User user = response.getBody();
//
//        fillLoversMap(id, entity, Objects.requireNonNull(user));
//        fillLovedMap(id, entity,Objects.requireNonNull(user));
//
//        return user;
//    }
//
//    private void fillLovedMap(long id, HttpEntity<String> entity, User user) {
//        String urlLoved = urlResource + "/loved/" + id;
//        HashMap<Long,User> lovedMap = user.getLoved();
//        ResponseEntity<User[]> responseLovers = restTemplate.exchange(urlLoved, HttpMethod.GET, entity, User[].class);
//        for (User u: Objects.requireNonNull(responseLovers.getBody())) {
//            lovedMap.put(u.getId(),u);
//        }
//    }
//
//    private void fillLoversMap(long id, HttpEntity<String> entity, User user) {
//        String urlLovers = urlResource + "/lovers/" + id;
//        HashMap<Long,User> loversMap = user.getLovers();
//        ResponseEntity<User[]> responseLovers = restTemplate.exchange(urlLovers, HttpMethod.GET, entity, User[].class);
//        for (User u: Objects.requireNonNull(responseLovers.getBody())) {
//            loversMap.put(u.getId(),u);
//        }
//    }
//


//}
