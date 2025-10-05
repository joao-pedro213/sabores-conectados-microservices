package com.postech.core.restaurant.controller;

import com.postech.core.account.datasource.IAccountDataSource;
import com.postech.core.helpers.RestaurantObjectMother;
import com.postech.core.restaurant.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.domain.usecase.CreateRestaurantUseCase;
import com.postech.core.restaurant.domain.usecase.DeleteRestaurantByIdUseCase;
import com.postech.core.restaurant.domain.usecase.RetrieveRestaurantByIdUseCase;
import com.postech.core.restaurant.domain.usecase.UpdateRestaurantUseCase;
import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import com.postech.core.restaurant.presenter.RestaurantPresenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {
    @Mock
    private IRestaurantDataSource mockRestaurantDataSource;

    @Mock
    private IAccountDataSource mockUserDataSource;

    @InjectMocks
    private RestaurantController controller;

    @Mock
    private RestaurantPresenter mockRestaurantPresenter;

    private MockedStatic<RestaurantPresenter> mockedStaticRestaurantPresenter;

    @BeforeEach
    void setUp() {
        this.mockedStaticRestaurantPresenter = mockStatic(RestaurantPresenter.class);
        this.mockedStaticRestaurantPresenter.when(RestaurantPresenter::build).thenReturn(this.mockRestaurantPresenter);
    }

    private static Map<String, Object> getSampleRestaurantData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "ownerId", UUID.randomUUID(),
                "managerIds", List.of(UUID.randomUUID()),
                "name", "test-restaurant",
                "address", "test address 123",
                "cuisineType", "MEXICAN",
                "businessHours", Map.of(DayOfWeek.FRIDAY, Map.of("openingTime", "10:00", "closingTime", "23:00")),
                "lastUpdated", LocalDateTime.parse("2025-09-17T00:00:00.000")
        );
    }

    @Test
    void shouldCreateRestaurant() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final NewRestaurantDto newRestaurantDto = RestaurantObjectMother.buildNewRestaurantDto(restaurantSampleData);
        final RestaurantEntity createdRestaurantEntity = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        final RestaurantDto createdRestaurantDto = RestaurantDto.builder().build();
        final CreateRestaurantUseCase mockCreateRestaurantUseCase = mock(CreateRestaurantUseCase.class);
        when(mockCreateRestaurantUseCase.execute(any(RestaurantEntity.class))).thenReturn(createdRestaurantEntity);
        when(this.mockRestaurantPresenter.toDto(createdRestaurantEntity)).thenReturn(createdRestaurantDto);
        try (MockedStatic<CreateRestaurantUseCase> mockedStaticCreateRestaurantUseCase = mockStatic(CreateRestaurantUseCase.class)) {
            mockedStaticCreateRestaurantUseCase.when(() -> CreateRestaurantUseCase.build(any(RestaurantGateway.class))).thenReturn(mockCreateRestaurantUseCase);

            // When
            final RestaurantDto restaurantDto = this.controller.createRestaurant(newRestaurantDto);

            // Then
            final ArgumentCaptor<RestaurantEntity> captor = ArgumentCaptor.forClass(RestaurantEntity.class);
            verify(mockCreateRestaurantUseCase, times(1)).execute(captor.capture());
            final RestaurantEntity capturedRestaurantEntity = captor.getValue();
            final RestaurantEntity expectedRestaurantEntity = createdRestaurantEntity.toBuilder().id(null).lastUpdated(null).build();
            assertThat(capturedRestaurantEntity).usingRecursiveComparison().isEqualTo(expectedRestaurantEntity);
            assertThat(restaurantDto).isNotNull().isEqualTo(createdRestaurantDto);
        }
    }

    @Test
    void shouldRetrieveRestaurantById() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final RestaurantEntity foundRestaurantEntity = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        final RetrieveRestaurantByIdUseCase mockRetrieveRestaurantByIdUseCase = mock(RetrieveRestaurantByIdUseCase.class);
        when(mockRetrieveRestaurantByIdUseCase.execute(UUID.fromString(restaurantSampleData.get("id").toString()))).thenReturn(foundRestaurantEntity);
        final RestaurantDto foundRestaurantDto = RestaurantDto.builder().build();
        when(this.mockRestaurantPresenter.toDto(foundRestaurantEntity)).thenReturn(foundRestaurantDto);
        try (MockedStatic<RetrieveRestaurantByIdUseCase> mockedStaticRetrieveRestaurantByIdUseCase = mockStatic(RetrieveRestaurantByIdUseCase.class)) {
            mockedStaticRetrieveRestaurantByIdUseCase.when(() -> RetrieveRestaurantByIdUseCase.build(any(RestaurantGateway.class))).thenReturn(mockRetrieveRestaurantByIdUseCase);

            // When
            final RestaurantDto restaurantDto = this.controller.retrieveRestaurantById(UUID.fromString(restaurantSampleData.get("id").toString()));

            // Then
            assertThat(restaurantDto).isNotNull().isEqualTo(foundRestaurantDto);
        }
    }

    @Test
    void shouldUpdateRestaurant() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final UpdateRestaurantDto updateRestaurantDto = RestaurantObjectMother.buildUpdateRestaurantDto(restaurantSampleData);
        final RestaurantEntity updatedRestaurantEntity = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        UpdateRestaurantUseCase mockUpdateRestaurantUseCase = mock(UpdateRestaurantUseCase.class);
        when(
                mockUpdateRestaurantUseCase
                        .execute(
                                eq(UUID.fromString(restaurantSampleData.get("id").toString())),
                                eq((List<UUID>) restaurantSampleData.get("managerIds")),
                                eq(updatedRestaurantEntity.getAddress()),
                                any(LinkedHashMap.class)))
                .thenReturn(updatedRestaurantEntity);
        final RestaurantDto updatedRestaurantDto = RestaurantDto.builder().build();
        when(this.mockRestaurantPresenter.toDto(updatedRestaurantEntity)).thenReturn(updatedRestaurantDto);
        try (MockedStatic<UpdateRestaurantUseCase> mockedStaticUpdateRestaurantUseCase = mockStatic(UpdateRestaurantUseCase.class)) {
            mockedStaticUpdateRestaurantUseCase.when(() -> UpdateRestaurantUseCase.build(any(RestaurantGateway.class))).thenReturn(mockUpdateRestaurantUseCase);

            // When
            final RestaurantDto restaurantDto = this.controller
                    .updateRestaurant(UUID.fromString(restaurantSampleData.get("id").toString()), updateRestaurantDto);

            // Then
            assertThat(restaurantDto).isNotNull().isEqualTo(updatedRestaurantDto);
        }
    }

    @Test
    void shouldDeleteRestaurantById() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        DeleteRestaurantByIdUseCase mockDeleteRestaurantByIdUseCase = mock(DeleteRestaurantByIdUseCase.class);
        try (MockedStatic<DeleteRestaurantByIdUseCase> mockedStaticDeleteRestaurantByIdUseCase = mockStatic(DeleteRestaurantByIdUseCase.class)) {
            mockedStaticDeleteRestaurantByIdUseCase.when(() -> DeleteRestaurantByIdUseCase.build(any(RestaurantGateway.class))).thenReturn(mockDeleteRestaurantByIdUseCase);

            // When
            this.controller.deleteRestaurantById(UUID.fromString(restaurantSampleData.get("id").toString()));

            // Then
            verify(mockDeleteRestaurantByIdUseCase, times(1)).execute(UUID.fromString(restaurantSampleData.get("id").toString()));
        }
    }

    @AfterEach
    void tearDown() {
        if (this.mockedStaticRestaurantPresenter != null) {
            this.mockedStaticRestaurantPresenter.close();
        }
    }
}
