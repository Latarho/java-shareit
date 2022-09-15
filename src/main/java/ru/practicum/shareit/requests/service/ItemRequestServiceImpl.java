package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestCreatingDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreatingDto itemRequestCreatingDto)
            throws UserNotFoundException {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestForCreate(itemRequestCreatingDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            itemRequest.setRequesterId(userId);
            itemRequest.setCreated(LocalDateTime.now());
            return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
        }
    }

    @Override
    public List<ItemRequestWithItemsDto> getByRequesterId(Long userId) throws UserNotFoundException,
            ItemRequestNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<ItemRequestWithItemsDto> listItemRequestDtoWithItems = new ArrayList<>();
            for (ItemRequest itemRequest : itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)) {
                ItemRequestWithItemsDto itemFound = getById(itemRequest.getId(), userId);
                listItemRequestDtoWithItems.add(itemFound);
            }
            return listItemRequestDtoWithItems;
        }
    }

    @Override
    public ItemRequestWithItemsDto getById(Long requestId, Long userId) throws UserNotFoundException,
            ItemRequestNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            if (itemRequestRepository.findById(requestId).isEmpty()) {
                throw new ItemRequestNotFoundException("Отсутствует запрос на вещь id: " + requestId);
            } else {
                List<ItemDto> itemsDto = new ArrayList<>();
                for (Item item : itemRepository.getItemsForRequest(requestId)) {
                    itemsDto.add(ItemMapper.toItemDto(item));
                }
                return ItemRequestMapper
                        .toItemRequestWithItemsDto(itemRequestRepository.findById(requestId).orElseThrow(), itemsDto);
            }
        }
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllWithPagination(Long userId, Integer from, Integer size)
            throws UserNotFoundException, ItemRequestNotFoundException {
        List<ItemRequestWithItemsDto> listItemRequestDtoWithItems = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId,
                PageRequest.of(from / size, size))) {
            ItemRequestWithItemsDto itemFound = getById(itemRequest.getId(), userId);
            listItemRequestDtoWithItems.add(itemFound);
        }
        return listItemRequestDtoWithItems;
    }
}