package ru.liga.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name="heading",nullable = false)
    private String heading;
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "description", nullable = false)
    private String description;

//
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JsonIgnoreProperties({"users"})
//    @JoinTable(name = "lovers"
//            , joinColumns = @JoinColumn(name = "user_id")
//            , inverseJoinColumns = @JoinColumn(name = "lover_id"))
//    private Set<User> thisLovers = new HashSet<>();

//    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "thisLovers")
//    @JsonIgnoreProperties({"users}"})
//    private Set<User> lovedThis = new HashSet<>();

//    public void addLovers(User user){
//        thisLovers.add(user);
//    }
//    public void removeLovers(User user){
//        thisLovers.remove(user);
//    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "gender = " + gender + ", " +
                "description = " + description + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 562048007;
    }
}
