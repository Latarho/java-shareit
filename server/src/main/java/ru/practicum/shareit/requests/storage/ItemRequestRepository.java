package ru.practicum.shareit.requests.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
        List<ItemRequest> findByRequesterOrderByCreatedDesc(User user);

        @Query("select i from ItemRequest i where i.requester <> ?1")
        List<ItemRequest> findAllItemRequestsByAnotherUser(User user, Pageable pageable);
}