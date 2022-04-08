package ru.liga.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class User {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("gender")
    private Gender gender;
    @JsonProperty("heading")
    private String heading;
    @JsonProperty("description")
    private String description;
    @JsonProperty("find")
    private FindGender find;
    private HashMap<Long, User> lovers = new HashMap<>();
    private HashMap<Long, User> loved = new HashMap<>();

    public User(Long id, String name, Gender gender, String heading, String description) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.heading = heading;
        this.description = description;
    }

    @Override
    public String toString() {
        return heading + " " + description;

    }

    public String toJson() {
        return "{" +
                "\"id\":" + id + ", " +
                "\"name\":\"" + name + "\", " +
                "\"heading\":\"" + heading + "\", " +
                "\"gender\":\"" + gender + "\", " +
                "\"description\":\"" + description + "\", " +
                "\"find\":\"" + find + "\"}";
    }
}
