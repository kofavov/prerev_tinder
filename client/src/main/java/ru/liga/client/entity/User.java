package ru.liga.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;

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
    private String gender;
    @JsonProperty("heading")
    private String heading;
    @JsonProperty("description")
    private String description;
    private HashMap<Long,User> lovers = new HashMap<>();
    private HashMap<Long,User> loved = new HashMap<>();

    public User(Long id, String name, String gender, String heading, String description) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.heading = heading;
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + ", " +
                "\"name\":\"" + name + "\", " +
                "\"heading\":\""+ heading + "\", " +
                "\"gender\":\"" + gender + "\", " +
                "\"description\":\"" + description + "\"}";
    }
}
