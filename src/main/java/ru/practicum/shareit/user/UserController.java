package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return UsersMapper.toUserDtoList(userService.getUsers());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable(name = "id") @Positive Integer id) {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid final User user) {
        return UserMapper.toUserDto(userService.create(user));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable(name = "id") @Positive final Integer id,
                          @RequestBody final User user) {
        return UserMapper.toUserDto(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable(name = "id") @Positive final Integer id) {
        userService.deleteById(id);
    }
}
