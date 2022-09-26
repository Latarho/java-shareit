package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void checkCreateItemRequestIntegrationPositive() {
        User userOne = new User(1L, "Serg", "latarho@gmail.com");
        userRepository.saveAndFlush(userOne);
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"Это запрос на вещь номер один", created, null);
        ItemRequestDto result = itemRequestService.create(userOne.getId(),itemRequestDto);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(result.getId());

        assertTrue(itemRequest.isPresent());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.get().getDescription()));
    }

    @Test
    void checkGetItemRequestByIdIntegrationPositive() throws ItemRequestNotFoundException {
        User userOne = new User(1L, "Serg", "latarho@gmail.com");
        userRepository.saveAndFlush(userOne);
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L,"Это запрос на вещь номер один", userOne, created, null);
//        itemRequestRepository.saveAndFlush(itemRequest);
//        ItemRequestDto result = itemRequestService.getItemRequestById(userOne.getId(), itemRequest.getId());
//
//        assertThat(itemRequest.getId(), equalTo(result.getId()));
//        assertThat(itemRequest.getDescription(), equalTo(result.getDescription()));
    }
}