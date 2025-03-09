package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getById(Integer id) {
        return userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден."));
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User update(Integer id, User user) {
        if (userExists(id)) {
            return userRepository.update(id, user);
        } else {
            throw new NotFoundException("Пользователь с id - " + id + "не найден.");
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (userExists(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("Пользователь с id - " + id + "не найден.");
        }
    }

    public boolean userExists(final Integer userId) {
        User user = getById(userId);

        return user != null;
    }
}
