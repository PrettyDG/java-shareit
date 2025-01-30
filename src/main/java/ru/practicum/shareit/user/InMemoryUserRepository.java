package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.info("Getting all users");

        return users.values();
    }

    @Override
    public Optional<User> getById(Integer id) {
        log.info("Getting user by id - {}", id);
        User user = users.get(id);

        if (user == null) {
            log.error("Пользователь с id - " + id + " не найден.");
            throw new NotFoundException("Пользователь с id - " + id + " не найден.");
        }

        return Optional.of(user);
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        log.info("Creating new user: {}", user);

        if (emailAlreadyExist(user.getEmail())) {
            log.error("Email уже зарегистрирован - " + user.getEmail());
            throw new IllegalArgumentException("Email уже зарегистрирован!");
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(Integer id, User user) {
        user.setId(id);
        log.info("Updating user: {}", user);

        users.remove(id);
        if (emailAlreadyExist(user.getEmail())) {
            log.error("Email уже зарегистрирован - " + user.getEmail());
            throw new IllegalArgumentException("Email уже зарегистрирован!");
        }

        users.put(id, user);

        return user;
    }

    @Override
    public void deleteById(Integer id) {
        log.info("Delete user by id - {}", id);

        users.remove(id);
    }

    public boolean emailAlreadyExist(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
