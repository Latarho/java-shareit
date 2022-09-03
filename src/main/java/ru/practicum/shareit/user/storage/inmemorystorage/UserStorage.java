package ru.practicum.shareit.user.storage.inmemorystorage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);
    User getById(Long id);
    List<User> getAll();
    User update(User user);
    void delete(Long id);
}