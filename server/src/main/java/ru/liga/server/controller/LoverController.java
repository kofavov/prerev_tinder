package ru.liga.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.entity.User;
import ru.liga.server.service.LoversService;
import ru.liga.server.service.UserService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class LoverController {
    private final LoversService loversService;

    public LoverController(LoversService loversService) {
        this.loversService = loversService;
    }

    @GetMapping("/lovers/{id}")
    public Set<User> getLoversById(@PathVariable("id") long id){
        log.info("getLoversById {}",id);
        return loversService.getLovers(id);
    }

    @GetMapping("/loved/{id}")
    public Set<User> getLovedThisById(@PathVariable("id")long id){
        log.info("getLovedById {}",id);
        return loversService.getLovedThisById(id);
    }

    @PostMapping("/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> addNewLover(@PathVariable("user_id") long userId,
                                                  @PathVariable("lover_id") long loverId){
        log.info("addNewLover userId {}, loverId {}",userId,loverId);
        loversService.addNewLover(userId, loverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/lovers/{user_id}/{lover_id}")
    public ResponseEntity<HttpStatus> deleteLover(@PathVariable("user_id") long userId,
                                                  @PathVariable("lover_id") long loverId){
        log.info("deleteLover userId {} loverId {}",userId,loverId);
        loversService.deleteLoverById(userId,loverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
