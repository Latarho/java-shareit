package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового запроса на вещь
     * @param userId идентификатор пользователя
     * @param itemRequestDto объект класса itemRequest
     * @return объект класса itemRequest (новый запрос на бронирование)
     */
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userRepository.checkAndReturnUserIfExist(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    /**
     * Получение информации по всем запросам на вещь для пользователя
     * @param userId идентификатор пользователя
     * @return список запросов
     */
    @Override
    public List<ItemRequestDto> getByRequesterId(Long userId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findByRequesterOrderByCreatedDesc(
                userRepository.checkAndReturnUserIfExist(userId)));
    }

    /**
     * Получение списка запросов на вещь по идентификатору
     * @param userId идентификатор пользователя
     * @param from количество результатов от 0 которые пропускаем
     * @param size количество результатов в ответе
     * @return список бронирований
     */
    @Override
    public List<ItemRequestDto> getById(Long userId, Integer from, Integer size) {
        Pageable pageRequest = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllItemRequestsByAnotherUser(
                userRepository.checkAndReturnUserIfExist(userId), pageRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequests);
    }

    /**
     * Получение информации по всем запросам на вещь для пользователя
     * @param userId идентификатор пользователя
     * @param requestId идентификатор бронирования
     * @return объект класса itemRequest (новый запрос на бронирование)
     */
    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) throws ItemRequestNotFoundException {
        userRepository.checkAndReturnUserIfExist(userId);
        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new ItemRequestNotFoundException(
                                        String.format("Отсутствует запрос на вещь id: " + requestId))));
    }
}