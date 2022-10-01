package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_idOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByBooker_idAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_idAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBooker_idAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                           LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b left join Item i on b.item.id = i.id "
            + "where i.ownerId = ?1 order by b.start desc")
    List<Booking> findForOwnerAllStatus(Long userId, Pageable pageable);

    @Query("select b from Booking b left join Item i on b.item.id = i.id "
            + "where i.ownerId = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findForOwnerStatus(Long userId, BookingStatus bookingStatus, Pageable pageable);

    @Query("select b from Booking b left join Item i on b.item.id = i.id "
            + "where i.ownerId = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findForOwnerPast(Long userId, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b left join Item i on b.item.id = i.id "
            + "where i.ownerId = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findForOwnerFuture(Long userId, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b left join Item i on b.item.id = i.id "
            + "where i.ownerId = ?1 and b.start < ?2 and b.end > ?3 order by b.start desc")
    List<Booking> findForOwnerCurrent(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "select * from bookings b left join items i on i.id = b.item_id "
            + "where b.item_id = ? and b.start_date_time > now() and b.status = 'APPROVED' and i.owner_id = ? "
            + "order by b.start_date_time - now() asc limit 1", nativeQuery = true)
    List<Booking> getNextBooking(Long itemId, Long userId);

    @Query(value = "select * from bookings b left join items i on i.id = b.item_id "
            + "where b.item_id = ? and b.end_date_time < now() and b.status = 'APPROVED' and i.owner_id = ? "
            + "order by now() - b.end_date_time asc limit 1", nativeQuery = true)
    List<Booking> getLastBooking(Long itemId, Long userId);

    @Query(value = "select * from bookings b "
            + "where b.item_id = ? and b.end_date_time < now() and b.status = 'APPROVED' and b.booker_id = ? ",
            nativeQuery = true)
    List<Booking> findForItem(Long itemId, Long userId);
}