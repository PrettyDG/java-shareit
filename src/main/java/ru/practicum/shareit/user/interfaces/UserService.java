package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getById(Integer id);

    User create(User user);

    User update(Integer id, User user);

    void deleteById(Integer id);
}
