package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

/**
 * Маппинг объекта класса Comment в CommentDto и наоборот.
 */
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentCreatingDto commentCreatingDto) {
        return new Comment(
                null,
                commentCreatingDto.getText(),
                null,
                null,
                null);
    }

    public static CommentForItemDto toCommentForItemDto(Comment comment) {
        return new CommentForItemDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}