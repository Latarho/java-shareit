package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    User user;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    BookingDto bookingDto;

    @BeforeEach
    void BeforeEach() {
        user = new User(1L, "Serg", "latarho@gmail.com");
        start = LocalDateTime.now().plusDays(3);
        end = LocalDateTime.now().plusDays(6);
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, null);
        bookingDto = new BookingDto(1L, start, end, item, user, BookingStatus.WAITING);
    }

    @Test
    void checkReturnBookingInfoAfterCreating() throws Exception {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);
        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingCreatingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is((bookingDto.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.end",
                        is((bookingDto.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.ownerId", is(bookingDto.getItem().getOwnerId()), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingDto.getItem().getRequestId())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void checkReturnUpdatedBookingInfoAfterUpdating() throws Exception {
        BookingDto bookingUpdatedDto = new BookingDto(1L, start, end, item, user, BookingStatus.APPROVED);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingUpdatedDto);
        mockMvc.perform(patch("/bookings/" + bookingDto.getId() + "?approved=true")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingUpdatedDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is((bookingUpdatedDto.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.end",
                        is((bookingUpdatedDto.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.item.id", is(bookingUpdatedDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingUpdatedDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingUpdatedDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingUpdatedDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.ownerId", is(bookingUpdatedDto.getItem().getOwnerId()),
                        Long.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingUpdatedDto.getItem().getRequestId())))
                .andExpect(jsonPath("$.booker.id", is(bookingUpdatedDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingUpdatedDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingUpdatedDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingUpdatedDto.getStatus().toString())));
    }

    @Test
    void checkReturnBookingInfoById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/" + bookingDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is((bookingDto.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.end",
                        is((bookingDto.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.ownerId", is(bookingDto.getItem().getOwnerId()), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingDto.getItem().getRequestId())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void checkReturnAllBookingsInfoForRequester() throws Exception {
        BookingDto bookingDtoTwo = new BookingDto(2L, start.minusDays(2), end.plusDays(2), item, user,
                BookingStatus.APPROVED);
        List<BookingDto> listBookings = new ArrayList<>();
        listBookings.add(bookingDto);
        listBookings.add(bookingDtoTwo);
        when(bookingService.getAllBookingsForRequesterWithPagination(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(listBookings);
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=5")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is((bookingDto.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[0].end",
                        is((bookingDto.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.ownerId", is(bookingDto.getItem().getOwnerId()),
                        Long.class))
                .andExpect(jsonPath("$[0].item.requestId", is(bookingDto.getItem().getRequestId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$[1].start",
                        is((bookingDtoTwo.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[1].end",
                        is((bookingDtoTwo.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[1].item.id", is(bookingDtoTwo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookingDtoTwo.getItem().getName())))
                .andExpect(jsonPath("$[1].item.description", is(bookingDtoTwo.getItem().getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(bookingDtoTwo.getItem().getAvailable())))
                .andExpect(jsonPath("$[1].item.ownerId", is(bookingDtoTwo.getItem().getOwnerId()),
                        Long.class))
                .andExpect(jsonPath("$[1].item.requestId", is(bookingDtoTwo.getItem().getRequestId())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingDtoTwo.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.name", is(bookingDtoTwo.getBooker().getName())))
                .andExpect(jsonPath("$[1].booker.email", is(bookingDtoTwo.getBooker().getEmail())))
                .andExpect(jsonPath("$[1].status", is(bookingDtoTwo.getStatus().toString())));
    }

    @Test
    void checkReturnAllBookingsInfoForOwner() throws Exception {
        BookingDto bookingDtoTwo = new BookingDto(2L, start.minusDays(2), end.plusDays(2), item, user,
                BookingStatus.APPROVED);
        List<BookingDto> listBookings = new ArrayList<>();
        listBookings.add(bookingDto);
        listBookings.add(bookingDtoTwo);
        when(bookingService.getAllBookingsForOwnerWithPagination(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(listBookings);
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=5")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is((bookingDto.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[0].end",
                        is((bookingDto.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.ownerId", is(bookingDto.getItem().getOwnerId()),
                        Long.class))
                .andExpect(jsonPath("$[0].item.requestId", is(bookingDto.getItem().getRequestId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$[1].start",
                        is((bookingDtoTwo.getStart().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[1].end",
                        is((bookingDtoTwo.getEnd().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[1].item.id", is(bookingDtoTwo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookingDtoTwo.getItem().getName())))
                .andExpect(jsonPath("$[1].item.description", is(bookingDtoTwo.getItem().getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(bookingDtoTwo.getItem().getAvailable())))
                .andExpect(jsonPath("$[1].item.ownerId", is(bookingDtoTwo.getItem().getOwnerId()),
                        Long.class))
                .andExpect(jsonPath("$[1].item.requestId", is(bookingDtoTwo.getItem().getRequestId())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingDtoTwo.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.name", is(bookingDtoTwo.getBooker().getName())))
                .andExpect(jsonPath("$[1].booker.email", is(bookingDtoTwo.getBooker().getEmail())))
                .andExpect(jsonPath("$[1].status", is(bookingDtoTwo.getStatus().toString())));
    }
}