package ru.practicum.shareit.unit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemCreatingDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCreatingDtoTest {
    @Autowired
    private JacksonTester<ItemCreatingDto> json;

    @Test
    void checkBookingCreatingDto() throws Exception {
        ItemCreatingDto itemCreateDto = new ItemCreatingDto("Это вещь номер один", "Это описание вещи номер один",
                true, 1L);
        JsonContent<ItemCreatingDto> result = json.write(itemCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Это вещь номер один");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Это описание вещи номер один");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}