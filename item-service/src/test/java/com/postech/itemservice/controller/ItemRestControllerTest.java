package com.postech.itemservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.core.common.exception.BusinessException;
import com.postech.core.item.controller.ItemController;
import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;
import com.postech.itemservice.api.config.SecurityConfig;
import com.postech.itemservice.data.document.ItemDocument;
import com.postech.itemservice.data.repository.IItemRepository;
import com.postech.itemservice.dto.ItemResponseDto;
import com.postech.itemservice.dto.NewItemRequestDto;
import com.postech.itemservice.dto.UpdateItemRequestDto;
import com.postech.itemservice.mapper.IItemMapper;
import com.postech.itemservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRestController.class)
@Import({SecurityConfig.class, SecurityService.class, IItemRepository.class})
@ExtendWith(MockitoExtension.class)
class ItemRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemControllerFactory mockItemControllerFactory;

    @MockitoBean
    private IItemMapper mockMapper;

    @MockitoBean
    private IItemRepository mockRepository;

    @MockitoBean
    private SecurityService mockSecurityService;

    @Mock
    private ItemController mockItemController;

    private static final String ITEM_ID = "a2a9d8e7-2e4f-4a1b-9b1a-9a8d7c6e5f4b";

    private static final String RESTAURANT_ID = "22512557-6f41-4d6a-839c-6b61bdb4640c";

    private static final String IDENTITY_ID = "dc92890a-a32c-43d9-adf1-9e9662825b77";

    private static final String VALID_ROLE = "RESTAURANT_OWNER";

    private static final String VALID_WRITE_AUTHORITY = "SCOPE_item:write";

    private static final String INVALID_AUTHORITY = "INVALID_AUTHORITY";

    private static final String BUSINESS_EXCEPTION_MESSAGE = "Business rule violation";

    @BeforeEach
    void setUp() {
        when(this.mockItemControllerFactory.build()).thenReturn(this.mockItemController);
        ItemDocument itemDocument = ItemDocument.builder().restaurantId(UUID.fromString(RESTAURANT_ID)).build();
        when(this.mockRepository.findById(UUID.fromString(ITEM_ID))).thenReturn(Optional.of(itemDocument));
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(true);
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForWriteOperations")
    void shouldCreate(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);
        final String requestBody = this.readJsonFromFile("new-item-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("new-item-response-body.json");
        final NewItemDto mappedNewItemDto = NewItemDto.builder().build();
        when(this.mockMapper.toNewItemDto(any(NewItemRequestDto.class))).thenReturn(mappedNewItemDto);
        final ItemDto itemDto = ItemDto.builder().build();
        when(this.mockItemController.createItem(mappedNewItemDto)).thenReturn(itemDto);
        final ItemResponseDto itemResponseDto = createItemResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toItemResponseDto(itemDto)).thenReturn(itemResponseDto);
        RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, role, validAuthority));

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
    }

    @Test
    void createShouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        // Given
        this.mockIdentityRole("RESTAURANT_OWNER");
        final String requestBody = this.readJsonFromFile("new-item-request-body.json");
        final NewItemDto mappedNewItemDto = NewItemDto.builder().build();
        when(this.mockMapper.toNewItemDto(any(NewItemRequestDto.class))).thenReturn(mappedNewItemDto);
        when(this.mockItemController.createItem(mappedNewItemDto)).thenThrow(new BusinessException(BUSINESS_EXCEPTION_MESSAGE));
        final RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, VALID_WRITE_AUTHORITY));

        //When & Then
        testBusinessExceptionScenario(requestBuilder);
    }

    @Test
    void createShouldReturnUnauthorizedWhenAuthorizationIsMissing() throws Exception {
        final String requestBody = this.readJsonFromFile("new-item-request-body.json");
        testUnauthorizedAccessScenario(post("/").contentType(MediaType.APPLICATION_JSON).content(requestBody));
    }

    @Test
    void createShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        final String requestBody = this.readJsonFromFile("new-item-request-body.json");
        final RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void createShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final String requestBody = this.readJsonFromFile("new-item-request-body.json");
        final RequestBuilder requestBuilder = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, VALID_WRITE_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void shouldRetrieveByRestaurantId() throws Exception {
        // Given
        final String expectedResponseBody = this.readJsonFromFile("items-by-restaurant-id-response-body.json");
        final List<ItemDto> itemDtoList = List.of(ItemDto.builder().build(), ItemDto.builder().build(), ItemDto.builder().build());
        when(this.mockItemController.retrieveItemsByRestaurantId(UUID.fromString(RESTAURANT_ID))).thenReturn(itemDtoList);
        final List<ItemResponseDto> itemResponseDtos = jsonToListOfMap(expectedResponseBody)
                .stream()
                .map(ItemRestControllerTest::createItemResponseDto)
                .toList();
        when(this.mockMapper.toItemResponseDto(any(ItemDto.class)))
                .thenReturn(itemResponseDtos.get(0), itemResponseDtos.get(1), itemResponseDtos.get(2));

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(get("/restaurant/{restaurantId}/menu", RESTAURANT_ID))
                .andExpect(status().isOk())
                .andReturn();
        List<Map<String, Object>> actualResponse = jsonToListOfMap(mvcResult.getResponse().getContentAsString());
        List<Map<String, Object>> expectedResponse = jsonToListOfMap(expectedResponseBody);
        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForWriteOperations")
    void shouldUpdateById(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);
        final String requestBody = this.readJsonFromFile("update-item-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("update-item-response-body.json");
        final UpdateItemDto mappedUpdateItemDto = UpdateItemDto.builder().build();
        when(this.mockMapper.toUpdateItemDto(any(UpdateItemRequestDto.class))).thenReturn(mappedUpdateItemDto);
        final ItemDto itemDto = ItemDto.builder().build();
        when(this.mockItemController.updateItem(UUID.fromString(ITEM_ID), mappedUpdateItemDto)).thenReturn(itemDto);
        final ItemResponseDto itemResponseDto = createItemResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toItemResponseDto(itemDto)).thenReturn(itemResponseDto);
        final RequestBuilder requestBuilder = put("/{id}", ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, role, validAuthority));

        //When & Then
        final MvcResult mvcResult = this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
    }

    @Test
    void updateByIdShouldReturnUnauthorizedWhenAuthorizationIsMissing() throws Exception {
        final String requestBody = this.readJsonFromFile("update-item-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        testUnauthorizedAccessScenario(requestBuilder);
    }

    @Test
    void updateByIdShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        final String requestBody = this.readJsonFromFile("update-item-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void updateByIdShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final String requestBody = this.readJsonFromFile("update-item-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, VALID_WRITE_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForWriteOperations")
    void shouldDeleteById(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);

        // When & Then
        final RequestBuilder requestBuilder = delete("/{id}", ITEM_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, role, validAuthority));
        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
        verify(this.mockItemController).deleteItemById(UUID.fromString(ITEM_ID));
    }

    @Test
    void deleteByIdShouldReturnUnauthorizedWhenAuthorizationIsMissing() throws Exception {
        testUnauthorizedAccessScenario(delete("/{id}", ITEM_ID));
    }

    @Test
    void deleteByIdShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        final RequestBuilder requestBuilder = delete("/{id}", ITEM_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void deleteByIdShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        this.mockIdentityRole("RESTAURANT_OWNER");
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final RequestBuilder requestBuilder = delete("/{id}", ITEM_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, VALID_WRITE_AUTHORITY));
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
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
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
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
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

    private static List<Map<String, Object>> jsonToListOfMap(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<>() {
        });
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

    private static ItemResponseDto createItemResponseDto(Map<String, Object> sampleData) {
        return ItemResponseDto.builder()
                .id(UUID.fromString(sampleData.get("id").toString()))
                .restaurantId(UUID.fromString(sampleData.get("restaurantId").toString()))
                .name(sampleData.get("name").toString())
                .description(sampleData.get("description").toString())
                .price(new BigDecimal(sampleData.get("price").toString()))
                .availableOnlyAtRestaurant(Boolean.valueOf(sampleData.get("availableOnlyAtRestaurant").toString()))
                .photoPath(sampleData.get("photoPath").toString())
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

    private void mockIdentityRole(String role) {
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn("RESTAURANT_OWNER".equals(role));
        when(this.mockSecurityService.isRestaurantManager()).thenReturn("RESTAURANT_MANAGER".equals(role));
    }

    private static Stream<Arguments> provideRoleAndAuthorityForWriteOperations() {
        return Stream.of(
                Arguments.of("RESTAURANT_OWNER", VALID_WRITE_AUTHORITY),
                Arguments.of("RESTAURANT_MANAGER", VALID_WRITE_AUTHORITY)
        );
    }
}
