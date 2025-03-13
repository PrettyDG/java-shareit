package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllItemsByUserOrderByIdAsc(User user);

    List<Item> findByRequestId(Integer id);

    @Query("""
            SELECT i FROM Item AS i
            WHERE i.available IS TRUE
            AND i.name ILIKE (CONCAT('%', :text, '%'))
            OR i.description ILIKE (CONCAT('%', :text, '%'))
            """)
    List<Item> search(String text);
}
