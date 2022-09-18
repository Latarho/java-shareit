package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
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
public class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User userRequesterOne;
    User userRequesterTwo;
    ItemRequest itemRequestOne;
    ItemRequest itemRequestTwo;

    @BeforeEach
    void BeforeEach() {
        userRequesterOne = new User(1L, "UserOwnerOne", "latarho@gmail.com");
        userRequesterTwo = new User(2L, "UserOwnerTwo", "latarho1@gmail.com");
        LocalDateTime createdFirst = LocalDateTime.now().minusDays(1L);
        itemRequestOne = new ItemRequest(1L, "Это описание запроса номер один",
                userRequesterOne, createdFirst, null);
        LocalDateTime createdSecond = LocalDateTime.now().minusDays(2L);
        itemRequestTwo = new ItemRequest(2L, "Это описание запроса номер два",
                userRequesterOne, createdSecond, null);
    }

    @Test
    void checkFindRequestListForUserOrderByCreatedDesc() {
        userRepository.save(userRequesterOne);

        itemRequestRepository.save(itemRequestOne);
        itemRequestRepository.save(itemRequestTwo);

        List<ItemRequest> foundRequests = itemRequestRepository.findByRequesterOrderByCreatedDesc(userRequesterOne);
        List<ItemRequest> listToCompare = List.of(itemRequestOne, itemRequestTwo);

        assertThat(foundRequests, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindRequestListForNotUser() {
        userRepository.save(userRequesterOne);
        userRepository.save(userRequesterTwo);

        itemRequestRepository.save(itemRequestOne);
        itemRequestTwo.setRequester(userRequesterTwo);
        itemRequestRepository.save(itemRequestTwo);

        List<ItemRequest> foundRequests = itemRequestRepository.findAllItemRequestsByAnotherUser(userRequesterOne,
                Pageable.unpaged());
        List<ItemRequest> listToCompare = List.of(itemRequestTwo);

        assertThat(foundRequests, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindRequestListForNotUserOrderByCreatedDesc() {
        userRepository.save(userRequesterOne);
        userRepository.save(userRequesterTwo);

        itemRequestRepository.save(itemRequestOne);
        itemRequestTwo.setRequester(userRequesterTwo);
        itemRequestRepository.save(itemRequestTwo);

        List<ItemRequest> foundRequests = itemRequestRepository.findAllItemRequestsByAnotherUser(userRequesterTwo,
                Pageable.unpaged());
        List<ItemRequest> listToCompare = List.of(itemRequestOne);

        assertThat(foundRequests, is(equalTo(listToCompare)));
    }
}