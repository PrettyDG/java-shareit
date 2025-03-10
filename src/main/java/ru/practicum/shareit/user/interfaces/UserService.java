package ru.practicum.shareit.user.interfaces;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;

import java.util.Collection;

@Transactional(readOnly = true)
public interface UserService {
    Collection<User> getUsers();

    User getById(Integer id);

    @Transactional
    User create(User user);

    @Transactional
    User update(Integer id, User user);

    @Transactional
    void deleteById(Integer id);
}
