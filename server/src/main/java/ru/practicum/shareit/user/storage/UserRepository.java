package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    default User checkAndReturnUserIfExist(Long userId) {
        return findById(userId).orElseThrow(
                () -> new UserNotFoundException("Отсутствует пользователь id: " + userId));
    }
}