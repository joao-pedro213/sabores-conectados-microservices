package com.postech.identityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.identityservice.api.config.SecurityConfig;
import com.postech.identityservice.data.document.enumerator.SystemRole;
import com.postech.identityservice.dto.ChangePasswordRequestDto;
import com.postech.identityservice.dto.NewIdentityRequestDto;
import com.postech.identityservice.service.IdentityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdentityRestController.class)
@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class IdentityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdentityService identityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShouldReturnCreatedWhenSuccessful() throws Exception {
        // Given
        NewIdentityRequestDto requestDto = NewIdentityRequestDto
                .builder()
                .role(SystemRole.CUSTOMER)
                .username("test-user")
                .password("Str0ngP@ssw0rd!")
                .build();
        UUID expectedId = UUID.randomUUID();
        when(this.identityService.create(any(NewIdentityRequestDto.class))).thenReturn(expectedId);

        // When & Then
        MvcResult result = this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(requestDto))
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .jwt()
                                                .authorities(new SimpleGrantedAuthority("SCOPE_identity:write"))))
                .andExpect(status().isCreated())
                .andReturn();
        UUID actualId = this.objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    void createShouldReturnForbiddenWhenMissingScope() throws Exception {
        // Given
        NewIdentityRequestDto requestDto = NewIdentityRequestDto
                .builder()
                .role(SystemRole.CUSTOMER)
                .username("test-user")
                .password("Str0ngP@ssw0rd!")
                .build();

        // When & Then
        this.mockMvc.
                perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(requestDto))
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .jwt()
                                                .authorities(new SimpleGrantedAuthority("INVALID_SCOPE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePasswordShouldReturnNoContentWhenSuccessful() throws Exception {
        // Given
        ChangePasswordRequestDto requestDto = ChangePasswordRequestDto
                .builder()
                .username("test-user")
                .oldPassword("0ldPa$$word")
                .newPassword("n3wP4assword!")
                .build();
        doNothing().when(this.identityService).changePassword("user", "oldPassword", "newPassword");

        // When & Then
        this.mockMvc.perform(post("/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePasswordShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        // Given
        ChangePasswordRequestDto requestDto = ChangePasswordRequestDto.builder().build();

        // When & Then
        this.mockMvc
                .perform(
                        post("/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
