package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequestorId(Integer requestorId, Sort sort);

    List<ItemRequest> findByRequestorIdNot(Integer requestorId, Sort sort);
}