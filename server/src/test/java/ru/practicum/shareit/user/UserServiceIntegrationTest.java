package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkCreateUserIntegrationPositive() throws ValidationException {
        UserDto userDto = new UserDto(1L, "Serg", "latarho@gmail.com");
        UserDto userDtoResult = userService.create(userDto);
        List<User> users = userRepository.findAll();

        assertFalse(users.isEmpty());
        assertThat(userDtoResult.getId(), equalTo(users.get(0).getId()));
        assertThat(userDtoResult.getName(), equalTo(users.get(0).getName()));
        assertThat(userDtoResult.getEmail(), equalTo(users.get(0).getEmail()));
    }
}