package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collection;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден."));
    }

    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Integer id, User user) {
        if (!userExists(id)) {
            throw new NotFoundException("Пользователь с id - " + id + "не найден.");
        } else {
            User oldUser = getById(id);

            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }

            return userRepository.save(oldUser);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (userExists(id)) {
            userRepository.deleteById(id);
        }
    }

    public boolean userExists(final Integer userId) {
        User user = getById(userId);

        return user != null;
    }
}
