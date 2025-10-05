package com.postech.restaurantservice.data;

import com.postech.restaurantservice.data.document.RestaurantDocument;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorCallback implements BeforeConvertCallback<RestaurantDocument> {
    @Override
    public RestaurantDocument onBeforeConvert(RestaurantDocument userModel, String collection) {
        if (userModel.getId() == null) {
            userModel.setId(UUID.randomUUID());
        }
        return userModel;
    }
}
