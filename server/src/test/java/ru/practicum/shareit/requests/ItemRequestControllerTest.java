package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.controller.ItemRequestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void checkReturnRequestInfoAfterCreating() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Это запрос на вещь номер один", created, null);
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests").content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is((itemRequestDto.getCreated().toString()).replaceAll("0+$", ""))));
    }

    @Test
    void checkReturnRequestInfoByRequesterId() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        List<ItemRequestDto> listRequests = new ArrayList<>();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Это запрос на вещь номер один", created, null);
        listRequests.add(itemRequestDto);
        when(itemRequestService.getByRequesterId(anyLong())).thenReturn(listRequests);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is((itemRequestDto.getCreated().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto.getItems())));
    }

    @Test
    void checkReturnRequestInfoById() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Это запрос на вещь номер один", created, null);
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/" + itemRequestDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is((itemRequestDto.getCreated().toString()).replaceAll("0+$", ""))))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems())));
    }
}