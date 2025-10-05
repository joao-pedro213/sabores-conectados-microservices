package com.postech.restaurantservice.data.repository;

import com.postech.restaurantservice.data.document.RestaurantDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRestaurantRepository extends CrudRepository<RestaurantDocument, UUID> {
    Optional<RestaurantDocument> findByName(String name);
}
