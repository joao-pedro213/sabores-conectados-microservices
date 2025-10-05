package com.postech.reservationservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postech.core.reservation.controller.ReservationController;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.NewReservationDto;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.reservationservice.api.config.SecurityConfig;
import com.postech.reservationservice.dto.NewReservationRequestDto;
import com.postech.reservationservice.dto.ReservationResponseDto;
import com.postech.reservationservice.mapper.IReservationMapper;
import com.postech.reservationservice.service.SecurityService;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(ReservationRestController.class)
@Import({SecurityConfig.class, SecurityService.class})
@ExtendWith(MockitoExtension.class)
class ReservationRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReservationControllerFactory mockReservationControllerFactory;

    @MockitoBean
    private IReservationMapper mockMapper;

    @MockitoBean
    private SecurityService mockSecurityService;

    @Mock
    private ReservationController mockReservationController;

    private static final String RESTAURANT_ID = "44d3093e-2473-49cb-a16c-dac5fca455a1";
    private static final String RESERVATION_ID = "8e2dfb0a-cf57-4b54-a290-becb2ba80205";

    @BeforeEach
    void setUp() {
        when(this.mockReservationControllerFactory.build()).thenReturn(this.mockReservationController);
    }

    @Test
    void shouldCreateReservation() throws Exception {
        when(this.mockSecurityService.canCreateReservation()).thenReturn(Mono.just(true));
        when(this.mockSecurityService.getIdentityFromSecurityContext())
                .thenReturn(Mono.just(UUID.fromString("dc92890a-a32c-43d9-adf1-9e9662825b77")));
        final String requestBody = this.readJsonFromFile("new-reservation-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("new-reservation-response-body.json");
        final NewReservationDto mappedNewReservationDto = NewReservationDto.builder().build();
        when(this.mockMapper.toNewReservationDto(any(NewReservationRequestDto.class))).thenReturn(mappedNewReservationDto);
        final ReservationDto createdReservationDto = ReservationDto.builder().build();
        when(this.mockReservationController.createReservation(any(NewReservationDto.class))).thenReturn(Mono.just(createdReservationDto));
        final ReservationResponseDto reservationResponseDto = createReservationResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toReservationResponseDto(any(ReservationDto.class))).thenReturn(reservationResponseDto);

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
        final ReservationStatus status = ReservationStatus.PENDING;
        when(this.mockSecurityService.canReadRestaurant(any(UUID.class))).thenReturn(Mono.just(true));
        final String expectedResponseBody = readJsonFromFile("reservations-by-restaurant-id-response-body.json");
        when(this.mockReservationController.retrieveReservationByRestaurantId(UUID.fromString(RESTAURANT_ID), status))
                .thenReturn(Flux.just(ReservationDto.builder().build()));
        final List<ReservationResponseDto> reservationResponseDtos = jsonToListOfMap(expectedResponseBody)
                .stream()
                .map(ReservationRestControllerTest::createReservationResponseDto)
                .toList();
        when(this.mockMapper.toReservationResponseDto(any(ReservationDto.class))).thenReturn(reservationResponseDtos.getFirst());

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
    void changeStatusShouldUpdateReservation() throws IOException {
        when(this.mockSecurityService.canChangeReservationStatus(any(UUID.class))).thenReturn(Mono.just(true));
        final String requestBody = readJsonFromFile("change-status-request-body.json");
        final String expectedResponseBody = readJsonFromFile("change-status-response-body.json");
        final ReservationDto reservationDto = ReservationDto.builder().build();
        when(this.mockReservationController.changeReservationStatus(UUID.fromString(RESERVATION_ID), ReservationStatus.CONFIRMED))
                .thenReturn(Mono.just(reservationDto));
        final ReservationResponseDto reservationResponseDto = createReservationResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toReservationResponseDto(reservationDto)).thenReturn(reservationResponseDto);

        String actualResponse = this.webTestClient
                .mutateWith(mockJwt())
                .patch()
                .uri("/{id}/status", RESERVATION_ID)
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

    private static ReservationResponseDto createReservationResponseDto(Map<String, Object> sampleData) {
        return ReservationResponseDto
                .builder()
                .id(UUID.fromString(sampleData.get("id").toString()))
                .restaurantId(UUID.fromString(sampleData.get("restaurantId").toString()))
                .customerId(UUID.fromString(sampleData.get("customerId").toString()))
                .status(ReservationStatus.valueOf(sampleData.get("status").toString()))
                .people((int) sampleData.get("people"))
                .date(LocalDate.parse(sampleData.get("date").toString()))
                .time(LocalTime.parse(sampleData.get("time").toString()))
                .createdAt(LocalDateTime.parse(sampleData.get("createdAt").toString()))
                .build();
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

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        return getObjectMapper().readValue(json, new TypeReference<>() {
        });
    }

    private static List<Map<String, Object>> jsonToListOfMap(String json) throws IOException {
        return getObjectMapper().readValue(json, new TypeReference<>() {
        });
    }
}
