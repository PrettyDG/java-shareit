package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getUsers();

    Optional<User> getById(Integer id);

    User create(User user);

    User update(Integer id, User user);

    void deleteById(Integer id);
}
