package com.postech.restaurantservice.data.repository;

import com.postech.core.restaurant.domain.entity.enumerator.CuisineType;
import com.postech.restaurantservice.api.config.AuditConfig;
import com.postech.restaurantservice.data.UUIDGeneratorCallback;
import com.postech.restaurantservice.data.document.RestaurantDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import({UUIDGeneratorCallback.class, AuditConfig.class})
class RestaurantRepositoryITest extends TestContainerConfig {
    @Autowired
    private IRestaurantRepository repository;

    @Test
    void shouldFindRestaurantByLogin() {
        // Given
        RestaurantDocument sampleRestaurant = this.repository.save(createSampleRestaurant());
        UUID sampleRestaurantId = sampleRestaurant.getId();
        final String name = "Si Señor";

        // When
        Optional<RestaurantDocument> foundRestaurant = this.repository.findByName(name);

        // Then
        assertThat(foundRestaurant).isPresent();
        assertThat(foundRestaurant.get().getId()).isEqualTo(sampleRestaurantId);
        assertThat(foundRestaurant.get().getName()).isEqualTo(name);
        assertThat(foundRestaurant.get().getLastUpdated()).isNotNull();
    }

    private static RestaurantDocument createSampleRestaurant() {
        return RestaurantDocument
                .builder()
                .ownerId(UUID.randomUUID())
                .name("Si Señor")
                .address("test address 321")
                .cuisineType(CuisineType.MEXICAN)
                .businessHours(null)
                .build();
    }
}
