package com.postech.itemservice.data.repository;

import com.postech.itemservice.data.UUIDGeneratorCallback;
import com.postech.itemservice.data.document.ItemDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import({UUIDGeneratorCallback.class})
class ItemRepositoryITest extends TestContainerConfig {
    @Autowired
    private IItemRepository repository;

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        this.repository.save(createSampleItem(restaurantId));
        this.repository.save(createSampleItem(restaurantId));
        this.repository.save(createSampleItem(UUID.randomUUID()));

        // When
        List<ItemDocument> items = this.repository.findAllByRestaurantId(restaurantId);

        // Then
        assertThat(items).hasSize(2);
    }

    private static ItemDocument createSampleItem(UUID restaurantId) {
        return ItemDocument
                .builder()
                .restaurantId(restaurantId)
                .name("Test Item")
                .description("Test Description")
                .price(BigDecimal.TEN)
                .availableOnlyAtRestaurant(false)
                .photoPath("test-item.png")
                .build();
    }
}
