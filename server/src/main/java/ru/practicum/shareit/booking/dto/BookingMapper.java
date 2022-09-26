package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBookingCreate(BookingCreatingDto bookingCreatingDto) {
        return new Booking(
                null,
                bookingCreatingDto.getStart(),
                bookingCreatingDto.getEnd(),
                new Item(bookingCreatingDto.getItemId(), null, null, null, null, null),
                null,
                null
        );
    }

    public static BookingItemDto toBookingDtoItem(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}