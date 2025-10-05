package com.postech.accountservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.accountservice.api.config.SecurityConfig;
import com.postech.accountservice.dto.AccountResponseDto;
import com.postech.accountservice.dto.NewAccountRequestDto;
import com.postech.accountservice.dto.UpdateAccountRequestDto;
import com.postech.accountservice.mapper.IAccountMapper;
import com.postech.accountservice.service.IdentityService;
import com.postech.accountservice.service.SecurityService;
import com.postech.core.account.controller.AccountController;
import com.postech.core.account.dto.AccountDto;
import com.postech.core.account.dto.NewAccountDto;
import com.postech.core.account.dto.UpdateAccountDto;
import com.postech.core.common.exception.BusinessException;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountRestController.class)
@Import({SecurityConfig.class, SecurityService.class})
@ExtendWith(MockitoExtension.class)
class AccountRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountControllerFactory mockControllerFactory;

    @MockitoBean
    private IAccountMapper mockMapper;

    @MockitoBean
    private IdentityService mockIdentityService;

    @MockitoBean
    private SecurityService mockSecurityService;

    @Mock
    private AccountController mockAccountController;

    private static final String ACCOUNT_ID = "dc92890a-a32c-43d9-adf1-9e9662825b77";

    private static final String IDENTITY_ID = "6ed6ce23-0715-4592-823e-fb9cbd992a19";

    private static final String VALID_ROLE = "CUSTOMER";

    private static final String VALID_READ_AUTHORITY = "SCOPE_account:read";

    private static final String VALID_WRITE_AUTHORITY = "SCOPE_account:write";

    private static final String INVALID_AUTHORITY = "INVALID_AUTHORITY";

    private static final String BUSINESS_EXCEPTION_MESSAGE = "Business rule violation";

    @BeforeEach
    void setUp() {
        when(this.mockControllerFactory.build()).thenReturn(this.mockAccountController);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(true);
    }

    @Test
    void shouldCreate() throws Exception {
        // Given
        final String requestBody = this.readJsonFromFile("new-account-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("new-account-response-body.json");
        final NewAccountDto mappedNewAccountDto = NewAccountDto.builder().build();
        when(this.mockMapper.toNewAccountDto(any(NewAccountRequestDto.class))).thenReturn(mappedNewAccountDto);
        when(this.mockIdentityService.create(any())).thenReturn(UUID.randomUUID());
        final AccountDto accountDto = AccountDto.builder().build();
        when(this.mockAccountController.createAccount(mappedNewAccountDto)).thenReturn(accountDto);
        final AccountResponseDto accountResponseDto = createAccountResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toAccountResponseDto(accountDto)).thenReturn(accountResponseDto);

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
    }

    @Test
    void createShouldReturnBadRequestWhenRequiredBodyParametersAreMissing() throws Exception {
        // Given
        final String requestBody = this.readJsonFromFile("new-account-request-body-without-required-parameters.json");
        final String expectedResponseBody = this.readJsonFromFile("response-body-when-required-parameters-are-missing.json");

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
    }

    @Test
    void createShouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        // Given
        final String requestBody = this.readJsonFromFile("new-account-request-body.json");
        final NewAccountDto mappedNewAccountDto = NewAccountDto.builder().build();
        when(this.mockMapper.toNewAccountDto(any(NewAccountRequestDto.class))).thenReturn(mappedNewAccountDto);
        when(this.mockIdentityService.create(any())).thenReturn(UUID.randomUUID());
        when(this.mockAccountController.createAccount(mappedNewAccountDto)).thenThrow(new BusinessException(BUSINESS_EXCEPTION_MESSAGE));

        //When & Then
        testBusinessExceptionScenario(post("/").contentType(MediaType.APPLICATION_JSON).content(requestBody));
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForReadOperations")
    void shouldRetrieveById(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(true);
        final String expectedResponseBody = this.readJsonFromFile("new-account-response-body.json");
        final AccountDto accountDto = AccountDto.builder().build();
        when(this.mockAccountController.retrieveAccountById(UUID.fromString(ACCOUNT_ID))).thenReturn(accountDto);
        final AccountResponseDto accountResponseDto = createAccountResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toAccountResponseDto(accountDto)).thenReturn(accountResponseDto);
        RequestBuilder requestBuilder = get("/{id}", ACCOUNT_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, role, validAuthority));

        //When & Then
        final MvcResult mvcResult = this.mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> actualResponse = jsonToMap(mvcResult.getResponse().getContentAsString());
        Map<String, Object> expectedResponse = jsonToMap(expectedResponseBody);
        assertThatResponseBodyMatches(expectedResponse, actualResponse);
    }

    @Test
    void retrieveByIdShouldReturnUnauthorizedWhenAuthorizationIsMissing() throws Exception {
        testUnauthorizedAccessScenario(get("/{id}", ACCOUNT_ID));
    }

    @Test
    void retrieveByIdShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        final RequestBuilder requestBuilder = get("/{id}", ACCOUNT_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void retrieveByIdShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final RequestBuilder requestBuilder = get("/{id}", ACCOUNT_ID)
                .with(createJwtRequestPostProcessor(UUID.randomUUID().toString(), VALID_ROLE, VALID_READ_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForWriteOperations")
    void shouldUpdateById(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(true);
        final String requestBody = this.readJsonFromFile("update-account-request-body.json");
        final String expectedResponseBody = this.readJsonFromFile("update-account-response-body.json");
        final UpdateAccountDto mappedUpdateAccountDto = UpdateAccountDto.builder().build();
        when(this.mockMapper.toUpdateAccountDto(any(UpdateAccountRequestDto.class))).thenReturn(mappedUpdateAccountDto);
        final AccountDto accountDto = AccountDto.builder().build();
        when(this.mockAccountController.updateAccount(UUID.fromString(ACCOUNT_ID), mappedUpdateAccountDto)).thenReturn(accountDto);
        final AccountResponseDto accountResponseDto = createAccountResponseDto(jsonToMap(expectedResponseBody));
        when(this.mockMapper.toAccountResponseDto(accountDto)).thenReturn(accountResponseDto);
        final RequestBuilder requestBuilder = put("/{id}", ACCOUNT_ID)
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
    void updateByIdShouldReturnUnauthorizedShenAuthorizationIsMissing() throws Exception {
        final String requestBody = this.readJsonFromFile("update-account-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        testUnauthorizedAccessScenario(requestBuilder);
    }

    @Test
    void updateByIdShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        final String requestBody = this.readJsonFromFile("update-account-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void updateByIdShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final String requestBody = this.readJsonFromFile("update-account-request-body.json");
        final RequestBuilder requestBuilder = put("/{id}", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(createJwtRequestPostProcessor(UUID.randomUUID().toString(), VALID_ROLE, VALID_WRITE_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @ParameterizedTest
    @MethodSource("provideRoleAndAuthorityForWriteOperations")
    void shouldDeleteById(String role, String validAuthority) throws Exception {
        // Given
        this.mockIdentityRole(role);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(true);

        // When & Then
        final RequestBuilder requestBuilder = delete("/{id}", ACCOUNT_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, role, validAuthority));
        this.mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
        verify(this.mockAccountController).deleteAccountById(UUID.fromString(ACCOUNT_ID));
    }

    @Test
    void deleteByIdShouldReturnUnauthorizedWhenAuthorizationIsMissing() throws Exception {
        testUnauthorizedAccessScenario(delete("/{id}", ACCOUNT_ID));
    }

    @Test
    void deleteByIdShouldReturnForbiddenWhenMissingRequiredAuthority() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        final RequestBuilder requestBuilder = delete("/{id}", ACCOUNT_ID)
                .with(createJwtRequestPostProcessor(IDENTITY_ID, VALID_ROLE, INVALID_AUTHORITY));
        testForbiddenAccessScenario(requestBuilder);
    }

    @Test
    void deleteByIdShouldReturnForbiddenWhenAccountIsNotOwner() throws Exception {
        when(this.mockSecurityService.isCustomer()).thenReturn(true);
        when(this.mockSecurityService.isResourceOwner(any(UUID.class))).thenReturn(false);
        final RequestBuilder requestBuilder = delete("/{id}", ACCOUNT_ID)
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

    private static AccountResponseDto createAccountResponseDto(Map<String, Object> sampleData) {
        return AccountResponseDto
                .builder()
                .id(UUID.fromString(sampleData.get("id").toString()))
                .identityId(UUID.fromString(sampleData.get("identityId").toString()))
                .name(sampleData.get("name").toString())
                .email(sampleData.get("email").toString())
                .address(sampleData.get("address").toString())
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
        when(this.mockSecurityService.isCustomer()).thenReturn("CUSTOMER".equals(role));
        when(this.mockSecurityService.isRestaurantOwner()).thenReturn("RESTAURANT_OWNER".equals(role));
        when(this.mockSecurityService.isRestaurantManager()).thenReturn("RESTAURANT_MANAGER".equals(role));
    }

    private static Stream<Arguments> provideRoleAndAuthorityForReadOperations() {
        return Stream.of(
                Arguments.of("CUSTOMER", VALID_READ_AUTHORITY),
                Arguments.of("RESTAURANT_OWNER", VALID_READ_AUTHORITY),
                Arguments.of("RESTAURANT_MANAGER", VALID_READ_AUTHORITY)
        );
    }

    private static Stream<Arguments> provideRoleAndAuthorityForWriteOperations() {
        return Stream.of(
                Arguments.of("CUSTOMER", VALID_WRITE_AUTHORITY),
                Arguments.of("RESTAURANT_OWNER", VALID_WRITE_AUTHORITY),
                Arguments.of("RESTAURANT_MANAGER", VALID_WRITE_AUTHORITY)
        );
    }
}
