package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingCreatingDtoTest {
    @Autowired
    private JacksonTester<BookingCreatingDto> json;

    @Test
    void checkBookingCreatingDto() throws Exception {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(
                LocalDateTime.of(2022, 9, 10, 07, 35),
                LocalDateTime.of(2022, 9, 15, 10, 00),
                1L);
        JsonContent<BookingCreatingDto> result = json.write(bookingCreatingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-10T07:35:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-09-15T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}