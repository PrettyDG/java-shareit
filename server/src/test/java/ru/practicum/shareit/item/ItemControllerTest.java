package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDtoRequest itemDtoRequest;
    private ItemDtoResponse itemDtoResponse;

    @BeforeEach
    void setUp() {
        itemDtoRequest = new ItemDtoRequest("Test Item", "Description", true, null);
        itemDtoResponse = new ItemDtoResponse(1, "Test Item", "Description", true, null, 1);
    }

    @Test
    void getById_whenItemExists_thenReturnItem() throws Exception {
        Mockito.when(itemService.get(anyInt(), anyInt())).thenReturn(new ItemDto(1, "Test Item", "Description", true, null, null, null, null));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getById_whenItemNotFound_thenReturn404() throws Exception {
        Mockito.when(itemService.get(anyInt(), anyInt())).thenThrow(new NotFoundException("Объект не найден"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    void getByOwner_whenItemsExist_thenReturnItems() throws Exception {
        Mockito.when(itemService.getByOwner(anyInt())).thenReturn(List.of(itemDtoResponse));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void getByOwner_whenNoItems_thenReturnEmptyList() throws Exception {
        Mockito.when(itemService.getByOwner(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void search_whenTextProvided_thenReturnItems() throws Exception {
        Mockito.when(itemService.search("Test")).thenReturn(List.of(itemDtoResponse));

        mockMvc.perform(get("/items/search?text=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void search_whenNoTextProvided_thenReturnEmptyList() throws Exception {
        Mockito.when(itemService.search("")).thenReturn(List.of());

        mockMvc.perform(get("/items/search?text="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_whenValidRequest_thenReturnCreatedItem() throws Exception {
        Mockito.when(itemService.create(any(), anyInt())).thenReturn(itemDtoResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_whenInvalidRequest_thenReturn400() throws Exception {
        itemDtoRequest.setName(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_whenValidRequest_thenReturnUpdatedItem() throws Exception {
        Mockito.when(itemService.update(anyInt(), any(), anyInt())).thenReturn(itemDtoResponse);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addComment_whenValidRequest_thenReturnComment() throws Exception {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1, "Nice comment!", "User", null);
        Mockito.when(itemService.addComment(anyInt(), anyInt(), any(CommentDtoRequest.class)))
                .thenReturn(commentDtoResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Nice comment!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("User"))
                .andExpect(jsonPath("$.authorName").value("Nice comment!"));
    }

}
