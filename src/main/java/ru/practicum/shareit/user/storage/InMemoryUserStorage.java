package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User create(User user) {
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private long generateUserId() {
        id++;
        return id;
    }
}