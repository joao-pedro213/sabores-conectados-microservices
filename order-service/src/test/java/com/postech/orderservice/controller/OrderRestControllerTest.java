package com.postech.orderservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.core.order.controller.OrderController;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.NewOrderDto;
import com.postech.core.order.dto.OrderDto;
import com.postech.orderservice.api.config.SecurityConfig;
import com.postech.orderservice.dto.NewOrderRequestDto;
import com.postech.orderservice.dto.OrderItemResponseDto;
import com.postech.orderservice.dto.OrderResponseDto;
import com.postech.orderservice.mapper.IOrderMapper;
import com.postech.orderservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(OrderRestController.class)
@Import({SecurityConfig.class, SecurityService.class})
@ExtendWith(MockitoExtension.class)
class OrderRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderControllerFactory mockOrderControllerFactory;

    @MockitoBean
    private IOrderMapper mockMapper;

    @MockitoBean
    private SecurityService mockSecurityService;

    @Mock
    private OrderController mockOrderController;

    private static final String RESTAURANT_ID = "44d3093e-2473-49cb-a16c-dac5fca455a1";

    private static final String ORDER_ID = "8e2dfb0a-cf57-4b54-a290-becb2ba80205";

    @BeforeEach
    void setUp() {
        when(this.mockOrderControllerFactory.build()).thenReturn(this.mockOrderController);
    }

    @Test
    void shouldCreateOrder() throws Exception {
        when(this.mockSecurityService.canCreateOrder()).thenReturn(Mono.just(true));
        when(this.mockSecurityService.getIdentityFromSecurityContext())
                .thenReturn(Mono.just(UUID.fromString("dc92890a-a32c-43d9-adf1-9e9662825b77")));
        final String requestBody = this.readJsonFromFile("new-order-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("new-order-response-body.json");
        final NewOrderDto mappedNewOrderDto = NewOrderDto.builder().build();
        when(this.mockMapper.toNewOrderDto(any(NewOrderRequestDto.class))).thenReturn(mappedNewOrderDto);
        final OrderDto createdOrderDto = OrderDto.builder().build();
        when(this.mockOrderController.createOrder(any(NewOrderDto.class))).thenReturn(Mono.just(createdOrderDto));
        final OrderResponseDto orderResponseDto = createOrderResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toOrderResponseDto(any(OrderDto.class))).thenReturn(orderResponseDto);

        String actualResponse = this.webTestClient
                .mutateWith(mockJwt())
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        Map<String, Object> actualResponseMap = jsonToMap(actualResponse);
        Map<String, Object> expectedResponseMap = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(actualResponseMap, expectedResponseMap);
    }

    @Test
    void retrieveByRestaurantId() throws IOException {
        final OrderStatus status = OrderStatus.PENDING;
        when(this.mockSecurityService.canReadRestaurant(UUID.fromString(RESTAURANT_ID))).thenReturn(Mono.just(true));
        final String expectedResponseBody = readJsonFromFile("orders-by-restaurant-id-response-body.json");
        when(this.mockOrderController.retrieveOrdersByRestaurantId(UUID.fromString(RESTAURANT_ID), status))
                .thenReturn(Flux.just(OrderDto.builder().build()));
        final List<OrderResponseDto> orderResponseDtos = jsonToListOfMap(expectedResponseBody)
                .stream()
                .map(OrderRestControllerTest::createOrderResponseDto)
                .toList();
        when(this.mockMapper.toOrderResponseDto(any(OrderDto.class))).thenReturn(orderResponseDtos.getFirst());

        String actualResponse = this.webTestClient
                .mutateWith(mockJwt())
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/restaurant/{restaurantId}/list")
                                .queryParam("status", status)
                                .build(RESTAURANT_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        List<Map<String, Object>> actualResponseListMap = jsonToListOfMap(actualResponse);
        List<Map<String, Object>> expectedResponseListMap = jsonToListOfMap(expectedResponseBody);
        assertThat(actualResponseListMap).usingRecursiveComparison().isEqualTo(expectedResponseListMap);
    }

    @Test
    void changeStatusShouldUpdateOrder() throws IOException {
        when(this.mockSecurityService.canChangeOrderStatus(UUID.fromString(ORDER_ID))).thenReturn(Mono.just(true));
        final String requestBody = readJsonFromFile("change-status-request-body.json");
        final String expectedResponseBody = readJsonFromFile("change-status-response-body.json");
        final OrderDto orderDto = OrderDto.builder().build();
        when(this.mockOrderController.changeOrderStatus(UUID.fromString(ORDER_ID), OrderStatus.ACCEPTED))
                .thenReturn(Mono.just(orderDto));
        final OrderResponseDto orderResponseDto = createOrderResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toOrderResponseDto(orderDto)).thenReturn(orderResponseDto);

        String actualResponse = this.webTestClient
                .mutateWith(mockJwt())
                .patch()
                .uri("/{id}/status", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        Map<String, Object> actualResponseMap = jsonToMap(actualResponse);
        Map<String, Object> expectedResponseMap = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(actualResponseMap, expectedResponseMap);
    }

    private static OrderResponseDto createOrderResponseDto(Map<String, Object> sampleData) {
        return OrderResponseDto
                .builder()
                .id(UUID.fromString(sampleData.get("id").toString()))
                .restaurantId(UUID.fromString(sampleData.get("restaurantId").toString()))
                .customerId(UUID.fromString(sampleData.get("customerId").toString()))
                .status(OrderStatus.valueOf(sampleData.get("status").toString()))
                .items(createOrderItemResponseDtoList((List<Map<String, Object>>) sampleData.get("items")))
                .createdAt(LocalDateTime.parse(sampleData.get("createdAt").toString()))
                .build();
    }

    private static List<OrderItemResponseDto> createOrderItemResponseDtoList(List<Map<String, Object>> sampleData) {
        return sampleData
                .stream()
                .map(sampleItem ->
                        OrderItemResponseDto
                                .builder()
                                .id(UUID.fromString(sampleItem.get("id").toString()))
                                .price(BigDecimal.valueOf(Double.parseDouble(sampleItem.get("price").toString())))
                                .quantity((int) sampleItem.get("quantity"))
                                .build())
                .toList();
    }

    private String readJsonFromFile(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("controller/" + filePath)) {
            if (inputStream == null) {
                throw new IOException("JSON file not found in classpath: " + filePath);
            }
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static void assertThatResponseBodyMatches(Map<String, Object> expectedResponseAsMap, Map<String, Object> actualResponseAsMap) {
        assertThat(actualResponseAsMap).usingRecursiveComparison().isEqualTo(expectedResponseAsMap);
    }

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private static List<Map<String, Object>> jsonToListOfMap(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
