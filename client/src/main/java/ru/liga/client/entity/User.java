package ru.liga.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
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
