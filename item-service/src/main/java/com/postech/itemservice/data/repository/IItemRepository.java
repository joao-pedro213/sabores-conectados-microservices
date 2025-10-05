package com.postech.itemservice.data.repository;

import com.postech.itemservice.data.document.ItemDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("itemRepository")
public interface IItemRepository extends CrudRepository<ItemDocument, UUID> {
    List<ItemDocument> findAllByRestaurantId(UUID restaurantId);
}
