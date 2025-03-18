package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UsersMapper {
    public static List<UserDto> toUserDtoList(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
