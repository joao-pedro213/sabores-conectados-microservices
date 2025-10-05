package com.postech.restaurantservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.core.common.exception.BusinessException;
import com.postech.core.restaurant.controller.RestaurantController;
import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.restaurantservice.api.config.SecurityConfig;
import com.postech.restaurantservice.data.document.RestaurantDocument;
import com.postech.restaurantservice.data.mapper.IRestaurantMapper;
import com.postech.restaurantservice.data.repository.IRestaurantRepository;
import com.postech.restaurantservice.dto.DailyScheduleResponseDto;
import com.postech.restaurantservice.dto.NewRestaurantRequestDto;
import com.postech.restaurantservice.dto.RestaurantResponseDto;
import com.postech.restaurantservice.dto.UpdateRestaurantRequestDto;
import com.postech.restaurantservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RestaurantRestController.class)
@Import({SecurityConfig.class, SecurityService.class, IRestaurantRepository.class})
@ExtendWith(MockitoExtension.class)
class RestaurantRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantControllerFactory mockRestaurantControllerFactory;

    @MockitoBean
    private IRestaurantMapper mockMapper;

    @MockitoBean
    private IRestaurantRepository mockRepository;

    @MockitoBean
    private SecurityService mockSecurityService;

    @Mock
    private RestaurantController mockRestaurantController;

    private static final String RESTAURANT_ID = "22512557-6f41-4d6a-839c-6b61bdb4640c";

    private static final String INVALID_RESTAURANT_ID = "d18ca560-6ca3-42d0-a8e9-92ef3b20f04f";

    private static final String USER_A_ID = "dc92890a-a32c-43d9-adf1-9e9662825b77";

    private static final String USER_B_ID = "cfe23198-7c4d-474e-9cc0-c11f07535eea";

    private static final String VALID_AUTHORITY = "SCOPE_restaurant:write";

    private static final String INVALID_AUTHORITY = "INVALID_AUTHORITY";

    private static final String VALID_ROLE = "RESTAURANT_OWNER";

    private static final String BUSINESS_EXCEPTION_MESSAGE = "Business rule violation";

    @BeforeEach
    void setUp() {
        when(this.mockRestaurantControllerFactory.build()).thenReturn(this.mockRestaurantController);
        when(this.mockRepository.findById(UUID.fromString(RESTAURANT_ID)))
                .thenReturn(Optional.of(RestaurantDocument.builder().ownerId(UUID.fromString(USER_A_ID)).build()));
    }

    @Test
    void shouldCreateRestaurant() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        final String requestBody = this.readJsonFromFile("new-restaurant-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("new-restaurant-response-body.json");
        final NewRestaurantDto mappedNewRestaurantDto = NewRestaurantDto.builder().build();
        when(this.mockMapper.toNewRestaurantDto(any(NewRestaurantRequestDto.class))).thenReturn(mappedNewRestaurantDto);
        final RestaurantDto createdRestaurantDto = RestaurantDto.builder().build();
        when(this.mockRestaurantController.createRestaurant(mappedNewRestaurantDto)).thenReturn(createdRestaurantDto);
        final RestaurantResponseDto restaurantResponseDto = createRestaurantResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toRestaurantResponseDto(any(RestaurantDto.class))).thenReturn(restaurantResponseDto);
        RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(actualResponse, expectedResponse);
    }

    @Test
    void create_shouldReturnBadRequest_whenBusinessExceptionIsThrown() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        final String requestBody = this.readJsonFromFile("new-restaurant-request-body.json");
        final NewRestaurantDto mappedNewRestaurantDto = NewRestaurantDto.builder().build();
        when(this.mockMapper.toNewRestaurantDto(any(NewRestaurantRequestDto.class))).thenReturn(mappedNewRestaurantDto);
        when(this.mockRestaurantController.createRestaurant(mappedNewRestaurantDto)).thenThrow(new BusinessException(BUSINESS_EXCEPTION_MESSAGE));
        RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        testBusinessExceptionScenario(requestBuilder);
    }

    @Test
    void create_shouldReturnUnauthorized_whenAuthorizationIsMissing() throws Exception {
        // Given
        final String requestBody = this.readJsonFromFile("new-restaurant-request-body.json");
        RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        //When & Then
        testUnauthorizedAccessScenario(requestBuilder);
    }

    @Test
    void create_shouldReturnForbidden_whenMissingRequiredAuthority() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        final String requestBody = this.readJsonFromFile("new-restaurant-request-body.json");
        RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, INVALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void shouldFindRestaurantById() throws Exception {
        // Given
        final String expectedResponseBody = this.readJsonFromFile("new-restaurant-response-body.json");
        final RestaurantDto foundRestaurant = RestaurantDto.builder().build();
        when(this.mockRestaurantController.retrieveRestaurantById(UUID.fromString(RESTAURANT_ID))).thenReturn(foundRestaurant);
        final RestaurantResponseDto restaurantResponseDto = createRestaurantResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toRestaurantResponseDto(any(RestaurantDto.class))).thenReturn(restaurantResponseDto);

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(get("/{id}", RESTAURANT_ID))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(actualResponse, expectedResponse);
    }

    @Test
    void shouldUpdateRestaurant() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(true);
        final String requestBody = this.readJsonFromFile("update-restaurant-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("update-restaurant-response-body.json");
        final UpdateRestaurantDto mappedUpdateRestaurantDto = UpdateRestaurantDto.builder().build();
        when(this.mockMapper.toUpdateRestaurantDto(any(UpdateRestaurantRequestDto.class))).thenReturn(mappedUpdateRestaurantDto);
        final RestaurantDto updatedRestaurantDto = RestaurantDto.builder().build();
        when(this.mockRestaurantController.updateRestaurant(UUID.fromString(RESTAURANT_ID), mappedUpdateRestaurantDto))
                .thenReturn(updatedRestaurantDto);
        final RestaurantResponseDto restaurantResponseDto = createRestaurantResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toRestaurantResponseDto(any(RestaurantDto.class))).thenReturn(restaurantResponseDto);
        RequestBuilder requestBuilder = put("/{id}", RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(actualResponse, expectedResponse);
    }

    @Test
    void update_shouldReturnUnauthorized_whenAuthorizationIsMissing() throws Exception {
        // Given
        final String requestBody = this.readJsonFromFile("update-restaurant-request-body.json");
        RequestBuilder requestBuilder = put("/{id}", RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        //When & Then
        testUnauthorizedAccessScenario(requestBuilder);
    }

    @Test
    void updateById_shouldReturnForbidden_whenMissingRequiredAuthority() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(true);
        final String requestBody = this.readJsonFromFile("update-restaurant-request-body.json");
        RequestBuilder requestBuilder = put("/{id}", RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, INVALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void updateById_shouldReturnForbidden_whenTheUserIsNotTheRestaurantOwner() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(false);
        final String requestBody = this.readJsonFromFile("update-restaurant-request-body.json");
        RequestBuilder requestBuilder = put("/{id}", RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_B_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void updateById_shouldReturnForbidden_whenTheRestaurantIsNotFound() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(INVALID_RESTAURANT_ID))).thenReturn(false);
        final String requestBody = this.readJsonFromFile("update-restaurant-request-body.json");
        RequestBuilder requestBuilder = put("/{id}", INVALID_RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void shouldDeleteRestaurantById() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(true);
        RequestBuilder requestBuilder = delete("/{id}", RESTAURANT_ID)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
        verify(this.mockRestaurantController, times(1)).deleteRestaurantById(UUID.fromString(RESTAURANT_ID));
    }

    @Test
    void deleteById_shouldReturnForbidden_whenMissingRequiredAuthority() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(true);
        RequestBuilder requestBuilder = delete("/{id}", RESTAURANT_ID)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, INVALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void deleteById_shouldReturnForbidden_whenTheUserIsNotTheRestaurantOwner() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(RESTAURANT_ID))).thenReturn(false);
        RequestBuilder requestBuilder = delete("/{id}", RESTAURANT_ID)
                .with(createJwtRequestPostProcessor(USER_B_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void deleteById_shouldReturnForbidden_whenTheRestaurantIsNotFound() throws Exception {
        // Given
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(UUID.fromString(INVALID_RESTAURANT_ID))).thenReturn(false);
        RequestBuilder requestBuilder = delete("/{id}", INVALID_RESTAURANT_ID)
                .with(createJwtRequestPostProcessor(USER_A_ID, VALID_ROLE, VALID_AUTHORITY));

        //When & Then
        testForbiddenAccessScenario(requestBuilder);
    }

    private void testBusinessExceptionScenario(RequestBuilder requestBuilder) throws Exception {
        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = getExpectedResponseForBusinessException(mvcResult.getRequest().getRequestURI());
        assertThatResponseBodyMatches(actualResponse, expectedResponse);
    }

    private void testUnauthorizedAccessScenario(RequestBuilder requestBuilder) throws Exception {
        //When & Then
        this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    private void testForbiddenAccessScenario(RequestBuilder requestBuilder) throws Exception {
        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = getExpectedResponseForForbiddenAccess(mvcResult.getRequest().getRequestURI());
        assertThatResponseBodyMatches(actualResponse, expectedResponse);
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

    private static Map<String, Object> getExpectedResponseForBusinessException(String instance) {
        return Map.of(
                "type", "about:blank",
                "title", "Business Exception",
                "status", 400,
                "detail", BUSINESS_EXCEPTION_MESSAGE,
                "instance", instance
        );
    }

    private static Map<String, Object> getExpectedResponseForForbiddenAccess(String instance) {
        return Map.of(
                "type", "about:blank",
                "title", "Access Denied",
                "status", 403,
                "detail", "Access to the resource is prohibited",
                "instance", instance
        );
    }

    private static RestaurantResponseDto createRestaurantResponseDto(Map<String, Object> sampleData) {
        Map<Object, Map<Object, Object>> businessHoursMap = (Map<Object, Map<Object, Object>>) sampleData.get("businessHours");
        Map<DayOfWeek, DailyScheduleResponseDto> businessHours = new LinkedHashMap<>();
        businessHoursMap
                .forEach((k, v) ->
                        businessHours.put(
                                DayOfWeek.valueOf(k.toString()),
                                DailyScheduleResponseDto
                                        .builder()
                                        .openingTime(LocalTime.parse(v.get("openingTime").toString()))
                                        .closingTime(LocalTime.parse(v.get("closingTime").toString())).build()));
        return RestaurantResponseDto
                .builder()
                .id(UUID.fromString(sampleData.get("id").toString()))
                .ownerId(UUID.fromString(sampleData.get("ownerId").toString()))
                .name(sampleData.get("name").toString())
                .address(sampleData.get("address").toString())
                .cuisineType(sampleData.get("cuisineType").toString())
                .businessHours(businessHours)
                .lastUpdated(LocalDateTime.parse(sampleData.get("lastUpdated").toString()))
                .build();
    }

    private static JwtRequestPostProcessor createJwtRequestPostProcessor(
            String identityId,
            String role,
            String authority) {
        return SecurityMockMvcRequestPostProcessors
                .jwt()
                .jwt(jwt -> jwt.claim("usr", identityId).claim("role", role))
                .authorities(new SimpleGrantedAuthority(authority));
    }
}
