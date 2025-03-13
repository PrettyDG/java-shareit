package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void dtoToComment_ShouldMapDtoToComment() {
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder()
                .text("Great item!")
                .build();

        User user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .user(user)
                .build();

        Comment result = CommentMapper.dtoToComment(dtoRequest, item, user);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(dtoRequest.getText());
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getAuthor()).isEqualTo(user);
    }

    @Test
    void commentToDtoResponse_ShouldMapCommentToDtoResponse() {
        User user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDtoResponse result = CommentMapper.commentToDtoResponse(comment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(comment.getId());
        assertThat(result.getText()).isEqualTo(comment.getText());
        assertThat(result.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(result.getCreated()).isEqualTo(comment.getCreated());
    }
}
