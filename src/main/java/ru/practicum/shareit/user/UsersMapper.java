package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UsersMapper {
    public static List<UserDto> toUserDtoList(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public static List<User> toUserList(Collection<UserDto> userDtos) {
        return userDtos.stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
    }
}
