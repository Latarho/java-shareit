package ru.practicum.shareit.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestCreatingDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    User user;
    Item item;
    ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void BeforeEach() {
        user = new User(1L, "Serg", "latarho@gmail.com");
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, 1L);
        itemRequestService = new ItemRequestServiceImpl(userRepository,
                itemRepository, itemRequestRepository);
    }

    @Test
    void checkCreateItemPositive() throws UserNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestCreatingDto itemRequestCreatingDto = new ItemRequestCreatingDto("Создание запроса вещи");
        ItemRequest itemRequest = new ItemRequest(1L, "Это запрос на вещь номер один", 2L, created);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestResult = itemRequestService.create(1L, itemRequestCreatingDto);

        assertThat(itemRequestResult.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestResult.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestResult.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void checkCreateItemUserNotFoundException() {
        ItemRequestCreatingDto itemRequestCreatingDto = new ItemRequestCreatingDto("Создание запроса вещи");
        assertThrows(UserNotFoundException.class, () -> itemRequestService.create(1L, itemRequestCreatingDto));
    }

    @Test
    void checkGetItemByIdPositive() throws UserNotFoundException, ItemRequestNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "Item request description", 2L, created);
        Item itemOne = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                false, 2L, null);
        Item itemTwo = new Item(2L, "Это вещь номер два", "Это описание вещи номер два",
                false, 2L, null);
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemOne);
        itemList.add(itemTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.getItemsForRequest(anyLong()))
                .thenReturn(itemList);
        ItemRequestWithItemsDto itemRequestResult = itemRequestService.getById(1L, 1L);

        assertThat(itemRequestResult.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestResult.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestResult.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void checkGetItemByIdItemRequestNotFoundException() {
        Item itemOne = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                false, 2L, null);
        Item itemTwo = new Item(2L, "Это вещь номер два", "Это описание вещи номер два",
                false, 2L, null);
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemOne);
        itemList.add(itemTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getById(1L, 1L));
    }

    @Test
    void checkGetItemByIdUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getById(1L, 1L));
    }

    @Test
    void checkGetAllItemsByRequesterIdPositive() throws UserNotFoundException, ItemRequestNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequestOne = new ItemRequest(1L, "Это запрос на вещь номер один", 1L, created);
        ItemRequest itemRequestTwo = new ItemRequest(2L, "Это запрос на вещь номер два", 1L, created);
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequestOne);
        itemRequestList.add(itemRequestTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(itemRequestList);
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestOne));
        List<ItemRequestWithItemsDto> itemRequestResult = itemRequestService.getByRequesterId(1L);

        assertThat(itemRequestResult.get(0).getId(), equalTo(itemRequestOne.getId()));
        assertThat(itemRequestResult.get(0).getDescription(), equalTo(itemRequestOne.getDescription()));
        assertThat(itemRequestResult.get(0).getCreated(), equalTo(itemRequestOne.getCreated()));
    }
}