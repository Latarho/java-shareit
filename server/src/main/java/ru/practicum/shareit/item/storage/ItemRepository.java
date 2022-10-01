package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where (upper(i.name) like upper(concat('%', ?1, '%')) "
            + "or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> search(String text, Pageable pageable);

    @Query(value = "select * from items i "
            + "inner join item_requests ir on i.request_id = ir.id "
            + "where i.request_id = ?",
            nativeQuery = true)
    List<Item> getItemsForRequest(Long requestId);

    List<Item> findAllByOrderByIdAsc(Pageable pageable);
}