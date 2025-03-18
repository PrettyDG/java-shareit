package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var result = userService.getUsers();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(user.getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = userService.getById(user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id - 99 не найден.");

        verify(userRepository, times(1)).findById(99);
    }

    @Test
    void create_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.create(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_ShouldUpdateAndReturnUser() {
        User updatedUser = User.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.update(user.getId(), updatedUser);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updatedUser.getName());
        assertThat(result.getEmail()).isEqualTo(updatedUser.getEmail());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getName().equals(updatedUser.getName()) &&
                        savedUser.getEmail().equals(updatedUser.getEmail())
        ));
    }


    @Test
    void update_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        User updatedUser = User.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99, updatedUser))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id - 99 не найден.");

        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteById(user.getId());

        verify(userRepository).findById(user.getId());
        verify(userRepository).deleteById(user.getId());
    }


    @Test
    void deleteById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteById(99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id - 99 не найден.");

        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void userExists_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        boolean result = userService.userExists(user.getId());

        assertThat(result).isTrue();
        verify(userRepository).findById(user.getId());
    }
}
