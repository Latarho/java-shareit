package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void BeforeEach() {
        user = new User(1L, "Serg", "latarho@gmail.com");
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
    }

    @Test
    void checkCreateItemPositive() throws UserNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"Это запрос на вещь номер один", created, null);
        ItemRequest itemRequest = new ItemRequest(1L, "Это запрос на вещь номер один", user, created, null);
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestResult = itemRequestService.create(1L, itemRequestDto);

        assertThat(itemRequestResult.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestResult.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestResult.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void checkGetItemByIdPositive() throws UserNotFoundException, ItemRequestNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        Item itemOne = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                false, 2L, null);
        Item itemTwo = new Item(2L, "Это вещь номер два", "Это описание вещи номер два",
                false, 2L, null);
        List<Item> itemList = List.of(itemOne, itemTwo);
        ItemRequest itemRequest = new ItemRequest(1L, "Это запрос на вещь номер один", user, created, itemList);
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestResult = itemRequestService.getItemRequestById(1L, 1L);

        assertThat(itemRequestResult.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestResult.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestResult.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void checkGetItemByIdItemRequestNotFoundException() {
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
    }

    @Test
    void checkGetAllItemsByRequesterIdPositive() throws UserNotFoundException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequestOne = new ItemRequest(1L, "Это запрос на вещь номер один", user, created, null);
        ItemRequest itemRequestTwo = new ItemRequest(2L, "Это запрос на вещь номер два", user, created, null);
        List<ItemRequest> itemRequestList = List.of(itemRequestOne, itemRequestTwo);
        Mockito
                .when(itemRequestRepository.findByRequesterOrderByCreatedDesc(any()))
                .thenReturn(itemRequestList);
        List<ItemRequestDto> itemRequestResult = itemRequestService.getByRequesterId(1L);

        assertThat(itemRequestResult.get(0).getId(), equalTo(itemRequestOne.getId()));
        assertThat(itemRequestResult.get(0).getDescription(), equalTo(itemRequestOne.getDescription()));
        assertThat(itemRequestResult.get(0).getCreated(), equalTo(itemRequestOne.getCreated()));
    }
}