package ru.liga.server.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.entity.User;
import ru.liga.server.service.UserService;

import java.util.List;
import java.util.Set;
@Slf4j
@RestController
@RequestMapping("/api")
public class MainController {
    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAll(){
        log.info("GET api/users");
        List<User> users = userService.getAll();
        log.info(users.toString());
        return users;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id){
        log.info("getUserById {}",id);
        ResponseEntity<User> response;
        try {
            response = new ResponseEntity<>(userService.getUserById(id),HttpStatus.OK);
        }catch (Exception e){
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping("/users")
    public void saveNewUser(@RequestBody User user) {
        log.info("saveNewUser {}",user.toString());
        userService.saveNewUser(user);
    }

    @PutMapping("/users")
    public void updateUser(@RequestBody User user){
        log.info("updateUser {}",user.toString());
        userService.updateUser(user);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id){
        log.info("deleteUserById {}",id);
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/users/lovers/{id}")
    public Set<User> getLoversById(@PathVariable("id") long id){
        log.info("getLoversById {}",id);
        return userService.getLovers(id);
    }

    @GetMapping("/users/loved/{id}")
    public Set<User> getLovedThisById(@PathVariable("id")long id){
        log.info("getLovedById {}",id);
        return userService.getLovedThisById(id);
    }

    @PostMapping("/users/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> addNewLover(@PathVariable("user_id") long user_id,
                                      @PathVariable("lover_id") long lover_id){
        log.info("addNewLover userId {}, loverId {}",user_id,lover_id);
        userService.addNewLover(user_id, lover_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> deleteLover(@PathVariable("user_id") long user_id,
                                                  @PathVariable("lover_id") long lover_id){
        log.info("deleteLover userId {} loverId {}",user_id,lover_id);
        userService.deleteLoverById(user_id,lover_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
