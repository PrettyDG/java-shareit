package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Illegal format of email address")
    private String email;
}
