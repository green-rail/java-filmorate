package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @With
    private final long id;
    @Email(message = "Некорректный email")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    private final String login;
    private String name;
    private LocalDate birthday;

    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE)
    private final Set<Long> friends = new HashSet<>();

    public boolean isFriend(Long id) {
        return friends.contains(id);
    }

    public boolean addFriend(Long id) {
        return friends.add(id);
    }

    public boolean removeFriend(Long id) {
        return friends.remove(id);
    }

    public Collection<Long> getFriends() {
        return Collections.unmodifiableCollection(friends);
    }
}
