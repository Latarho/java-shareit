package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    private User userOne;

    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "Serg", "latarho@gmail.com");
    }

    @Test
    void checkCreateUserPositive() throws ValidationException {
        UserDto userDto = new UserDto(1L, "Serg", "latarho@gmail.com");
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(userOne);
        UserDto userDtoResult = userService.create(userDto);

        assertThat(userDtoResult.getId(), equalTo(userDto.getId()));
        assertThat(userDtoResult.getName(), equalTo(userDto.getName()));
        assertThat(userDtoResult.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void checkGetUserByIdPositive() throws UserNotFoundException {
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userOne));
        UserDto userDtoResult = userService.getById(1L);

        assertThat(userDtoResult.getId(), equalTo(userOne.getId()));
        assertThat(userDtoResult.getName(), equalTo(userOne.getName()));
        assertThat(userDtoResult.getEmail(), equalTo(userOne.getEmail()));
    }

    @Test
    void checkGetUserByIdUserNotFoundException() throws UserNotFoundException {
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void checkGetAllUsersPositive() {
        User userTwo = new User(2L, "Pomytkin", "latarho1@gmail.com");
        List<User> listUsers = List.of(userOne, userTwo);
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.findAll())
                .thenReturn(listUsers);
        List<UserDto> userDtoResult = userService.getAll();

        assertThat(userDtoResult.get(0).getId(), equalTo(userOne.getId()));
        assertThat(userDtoResult.get(0).getName(), equalTo(userOne.getName()));
        assertThat(userDtoResult.get(0).getEmail(), equalTo(userOne.getEmail()));
        assertThat(userDtoResult.get(1).getId(), equalTo(userTwo.getId()));
        assertThat(userDtoResult.get(1).getName(), equalTo(userTwo.getName()));
        assertThat(userDtoResult.get(1).getEmail(), equalTo(userTwo.getEmail()));
    }

    @Test
    void checkUpdateUserPositive() throws EmailAlreadyExistsException {
        User userUpdated = new User(1L, "Serg Update", "latarho@gmail.com");
        UserDto userUpdatedDto = new UserDto(1L, "Serg Update", "latarho@gmail.com");
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(userUpdated);
        UserDto userDtoResult = userService.update(1L, userUpdatedDto);

        assertThat(userDtoResult.getId(), equalTo(userUpdatedDto.getId()));
        assertThat(userDtoResult.getName(), equalTo(userUpdatedDto.getName()));
        assertThat(userDtoResult.getEmail(), equalTo(userUpdatedDto.getEmail()));
    }

    @Test
    void checkUpdateUserEmailAlreadyExistsException() {
        UserDto userUpdatedDto = new UserDto(1L, "Serg Update", "latarho@gmail.com");
        User userWithEmail = new User(2L, "Serg Pomytkin", "latarho@gmail.com");
        List<User> listUser = List.of(userOne, userWithEmail);
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userOne));
        Mockito
                .when(userRepository.findAll())
                .thenReturn(listUser);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, userUpdatedDto));
    }
}