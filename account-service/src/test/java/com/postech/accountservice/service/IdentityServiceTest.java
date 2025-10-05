package com.postech.accountservice.service;

import com.postech.accountservice.dto.NewIdentityRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    private static final String IDENTITY_ROUTE = "/api/identity";

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private IdentityService identityService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(this.identityService, "identityRoute", IDENTITY_ROUTE);
    }

    @Test
    void createShouldReturnUUIDWhenApiCallIsSuccessful() {
        // Given
        final NewIdentityRequestDto requestDto = NewIdentityRequestDto.builder()
                .username("test-user")
                .password("testPassw0rd!")
                .role("CUSTOMER")
                .build();
        final UUID expectedUuid = UUID.randomUUID();

        when(this.restClient.post()).thenReturn(this.requestBodyUriSpec);
        when(this.requestBodyUriSpec.uri("/api/identity/")).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.body(any(NewIdentityRequestDto.class))).thenReturn(this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.responseSpec.body(UUID.class)).thenReturn(expectedUuid);

        // When
        final UUID actualUuid = this.identityService.create(requestDto);

        // Then
        assertThat(actualUuid).isNotNull().isEqualTo(expectedUuid);
    }
}
