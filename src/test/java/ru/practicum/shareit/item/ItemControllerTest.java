package ru.practicum.shareit.item;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    void checkReturnItemInfoAfterCreating() throws Exception {
        ItemCreatingDto itemCreatingDto = new ItemCreatingDto("Это вещь номер один", "Это описание вещи номер один",
                true, 1L);
        ItemDto itemDto = new ItemDto(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L);
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemCreatingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is((itemDto.getName()))))
                .andExpect(jsonPath("$.description", is((itemDto.getDescription()))))
                .andExpect(jsonPath("$.available", is((itemDto.getAvailable()))))
                .andExpect(jsonPath("$.requestId", is((itemDto.getRequestId())), Long.class));
    }

    @Test
    void checkReturnUpdatedItemInfoAfterPatching() throws Exception {
        ItemCreatingDto itemUpdatedCreateDto = new ItemCreatingDto("Это вещь номер один для апдейта",
                "Это описание вещи номер один для апдейта", true, 1L);
        ItemDto itemDto = new ItemDto(1L, "Это вещь номер один для апдейта",
                "Это описание вещи номер один для апдейта", true, 1L);
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/" + itemDto.getId()).header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemUpdatedCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is((itemDto.getName()))))
                .andExpect(jsonPath("$.description", is((itemDto.getDescription()))))
                .andExpect(jsonPath("$.available", is((itemDto.getAvailable()))))
                .andExpect(jsonPath("$.requestId", is((itemDto.getRequestId())), Long.class));
    }

    @Test
    void checkReturnItemInfoById() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Это вещь номер один", "Это описание вещи номер один", true,
                1L);
        ItemWithCommentDto itemWithCommentDto = new ItemWithCommentDto(1L, "Это вещь номер один",
                "Это описание вещи номер один", true, 1L, null, null,
                null);
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemWithCommentDto);
        mockMvc.perform(get("/items/" + itemDto.getId()).header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is((itemDto.getName()))))
                .andExpect(jsonPath("$.description", is((itemDto.getDescription()))))
                .andExpect(jsonPath("$.available", is((itemDto.getAvailable()))))
                .andExpect(jsonPath("$.requestId", is((itemDto.getRequestId())), Long.class));
    }

    @Test
    void checkReturnAllItemsInfo() throws Exception {
        ItemWithCommentDto itemWithCommentDto = new ItemWithCommentDto(1L, "Это вещь номер один",
                "Это описание вещи номер один", true, 1L, null, null,
                null);
        ItemWithCommentDto itemWithCommentDtoTwo = new ItemWithCommentDto(1L, "Это вещь номер два",
                "Это описание вещи номер два", false, 1L, null, null,
                null);
        List<ItemWithCommentDto> listItems = new ArrayList<>();
        listItems.add(itemWithCommentDto);
        listItems.add(itemWithCommentDtoTwo);
        when(itemService.getAllWithPagination(anyLong(), anyInt(), anyInt())).thenReturn(listItems);
        mockMvc.perform(get("/items?from=0&size=5").header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemWithCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is((itemWithCommentDto.getName()))))
                .andExpect(jsonPath("$[0].description", is((itemWithCommentDto.getDescription()))))
                .andExpect(jsonPath("$[0].available", is((itemWithCommentDto.getAvailable()))))
                .andExpect(jsonPath("$[0].requestId", is((itemWithCommentDto.getRequestId())),
                        Long.class))
                .andExpect(jsonPath("$[1].id", is(itemWithCommentDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is((itemWithCommentDtoTwo.getName()))))
                .andExpect(jsonPath("$[1].description", is((itemWithCommentDtoTwo.getDescription()))))
                .andExpect(jsonPath("$[1].available", is((itemWithCommentDtoTwo.getAvailable()))))
                .andExpect(jsonPath("$[1].requestId", is((itemWithCommentDtoTwo.getRequestId())),
                        Long.class));
    }

    @Test
    void checkReturnItemsFoundByText() throws Exception {
        ItemDto itemDtoOne = new ItemDto(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L);
        ItemDto itemDtoTwo = new ItemDto(1L, "Это вещь номер два", "Это описание вещи номер два",
                false, 1L);
        List<ItemDto> listItems = new ArrayList<>();
        listItems.add(itemDtoOne);
        listItems.add(itemDtoTwo);
        when(itemService.searchByTextWithPagination(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(listItems);
        mockMvc.perform(get("/items/search?text=item&from=0&size=5")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is((itemDtoOne.getName()))))
                .andExpect(jsonPath("$[0].description", is((itemDtoOne.getDescription()))))
                .andExpect(jsonPath("$[0].available", is((itemDtoOne.getAvailable()))))
                .andExpect(jsonPath("$[0].requestId", is((itemDtoOne.getRequestId())),
                        Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is((itemDtoTwo.getName()))))
                .andExpect(jsonPath("$[1].description", is((itemDtoTwo.getDescription()))))
                .andExpect(jsonPath("$[1].available", is((itemDtoTwo.getAvailable()))))
                .andExpect(jsonPath("$[1].requestId", is((itemDtoTwo.getRequestId())),
                        Long.class));
    }

    @Test
    void checkReturnCommentAfterCreatingSuccess() throws Exception {
        Item item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один", true, 1L,
                1L);
        CommentCreatingDto commentCreatingDto = new CommentCreatingDto("Это отзыв номер один");
        LocalDate created = LocalDate.now();
        CommentDto commentDto = new CommentDto(1L, "Это отзыв номер один", item, "Serg", created);
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentCreatingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is((commentDto.getText()))))
                .andExpect(jsonPath("$.created", is((commentDto.getCreated().toString()))))
                .andExpect(jsonPath("$.authorName", is((commentDto.getAuthorName()))));
    }
}