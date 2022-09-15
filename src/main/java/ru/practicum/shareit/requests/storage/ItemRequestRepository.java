package ru.practicum.shareit.requests.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
        List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long userId);
        List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}