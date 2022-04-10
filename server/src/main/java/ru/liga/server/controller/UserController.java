package ru.liga.server.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.entity.User;
import ru.liga.server.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET api/users");
        List<User> users = userService.getAll();
        log.info(users.toString());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long id) {
        log.info("getUserById {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public void saveNewUser(@RequestBody User user) {
        log.info("saveNewUser {}", user.toString());
        userService.saveNewUser(user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        log.info("updateUser {}", user.toString());
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        log.info("deleteUserById {}", id);
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
