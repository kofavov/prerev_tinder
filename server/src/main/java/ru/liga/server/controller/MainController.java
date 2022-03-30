package ru.liga.server.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.entity.User;
import ru.liga.server.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MainController {
    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAll(){
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") long id){
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public void saveNewUser(@RequestBody User user) {
        userService.saveNewUser(user);
    }

    @PutMapping("/users")
    public void updateUser(@RequestBody User user){
        userService.updateUser(user);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/users/lovers/{id}")
    public Set<User> getLoversById(@PathVariable("id") long id){
        return userService.getLovers(id);
    }

    @GetMapping("/users/loved/{id}")
    public Set<User> getLovedThisById(@PathVariable("id")long id){
        return userService.getLovedThisById(id);
    }

    @PostMapping("/users/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> addNewLover(@PathVariable("user_id") long user_id,
                                      @PathVariable("lover_id") long lover_id){
        userService.addNewLover(user_id, lover_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> deleteLover(@PathVariable("user_id") long user_id,
                                                  @PathVariable("lover_id") long lover_id){
        userService.deleteLoverById(user_id,lover_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}