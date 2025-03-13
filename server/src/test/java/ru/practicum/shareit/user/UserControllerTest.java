package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;
    private UserDto userDto;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void createUser_ShouldReturnUser() throws Exception {
        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getById(anyInt())).thenReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).getById(user.getId());
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(user.getId())))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User updatedUser = User.builder()
                .name("Updated John Doe")
                .email("updated@example.com")
                .build();

        when(userService.update(eq(user.getId()), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

        verify(userService).update(eq(user.getId()), any(User.class));
    }


    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteById(user.getId());

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(user.getId());
    }
}
