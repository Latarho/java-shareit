package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User userOwnerOne;
    private User userOwnerTwo;
    private Item itemOne;
    private Item itemTwo;
    private Item itemThree;

    @BeforeEach
    void beforeEach() {
        userOwnerOne = new User(1L, "UserOwnerOne", "latarho@gmail.com");
        userOwnerTwo = new User(2L, "UserOwnerTwo", "latarho1@gmail.com");
        itemOne = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, null);
        itemTwo = new Item(2L, "Это вещь номер два", "Это описание вещи номер два",
                false, 2L, null);
        itemThree = new Item(3L, "Это вещь номер три", "Это описание вещи номер три",
                true, 2L, null);
    }

    @Test
    void checkFindAllItemsOrderByIdAsc() {
        userRepository.save(userOwnerOne);
        userRepository.save(userOwnerTwo);

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
        itemRepository.save(itemThree);

        List<Item> foundItems = itemRepository.findAllByOrderByIdAsc(PageRequest.of(0, 5));
        List<Item> listToCompare = List.of(itemOne, itemTwo, itemThree);

        assertThat(foundItems, is(equalTo(listToCompare)));
    }

    @Test
    void checkSearchItemByTextName() {
        userRepository.save(userOwnerOne);
        userRepository.save(userOwnerTwo);

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
        itemRepository.save(itemThree);

        List<Item> foundItems = itemRepository.search("Это вещь номер од", PageRequest.of(0, 5));
        List<Item> listToCompare = List.of(itemOne);

        assertThat(foundItems, is(equalTo(listToCompare)));
    }

    @Test
    void checkSearchItemByTextDescription() {
        userRepository.save(userOwnerOne);
        userRepository.save(userOwnerTwo);

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
        itemRepository.save(itemThree);

        List<Item> foundItems = itemRepository.search("Это описание вещи номер од", PageRequest.of(0, 5));
        List<Item> listToCompare = List.of(itemOne);

        assertThat(foundItems, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindItemsForRequest() {
        userRepository.save(userOwnerOne);
        userRepository.save(userOwnerTwo);

        LocalDateTime created = LocalDateTime.now().minusDays(1L);
        List<Item> listItem = List.of(itemOne, itemTwo);
        ItemRequest itemRequest = new ItemRequest(1L, "Это описание запроса", userOwnerOne, created, listItem);
        itemRequestRepository.save(itemRequest);
        itemOne.setRequestId(1L);
        itemRepository.save(itemOne);
        itemTwo.setRequestId(1L);
        itemRepository.save(itemTwo);
        itemRepository.save(itemThree);

        List<Item> foundItems = itemRepository.getItemsForRequest(1L);
        List<Item> listToCompare = List.of(itemOne, itemTwo);

        assertThat(foundItems, is(equalTo(listToCompare)));
    }
}