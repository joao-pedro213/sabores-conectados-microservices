package com.postech.identityservice.service;

import com.postech.identityservice.api.exception.IdentityAlreadyExistsException;
import com.postech.identityservice.api.exception.InvalidCredentialsException;
import com.postech.identityservice.data.document.IdentityDocument;
import com.postech.identityservice.data.document.enumerator.SystemRole;
import com.postech.identityservice.data.repository.IIdentityRepository;
import com.postech.identityservice.dto.NewIdentityRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private IIdentityRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private IdentityService identityService;

    private NewIdentityRequestDto requestDto;
    private IdentityDocument identityDocument;

    @BeforeEach
    void setUp() {
        this.requestDto = NewIdentityRequestDto
                .builder()
                .username("test-user")
                .password("password")
                .role(SystemRole.CUSTOMER)
                .build();
        identityDocument = IdentityDocument
                .builder()
                .id(UUID.randomUUID())
                .username("test-user")
                .password("oldEncodedPassword")
                .role(SystemRole.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("Should create a new identity when username does not exist")
    void createShouldCreateNewIdentityWhenUsernameDoesNotExist() {
        // Given
        when(this.repository.findByUsername(this.requestDto.getUsername())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(this.requestDto.getPassword())).thenReturn("encodedPassword");
        when(this.repository.save(any(IdentityDocument.class))).thenReturn(this.identityDocument);

        // When
        UUID createdId = this.identityService.create(this.requestDto);

        // Then
        assertThat(createdId).isNotNull().isEqualTo(this.identityDocument.getId());
    }

    @Test
    @DisplayName("Should throw IdentityAlreadyExistsException when username already exists")
    void createShouldThrowIdentityAlreadyExistsExceptionWhenUsernameAlreadyExists() {
        // Given
        when(this.repository.findByUsername(this.requestDto.getUsername()))
                .thenReturn(Optional.of(this.identityDocument));

        // When & Then
        assertThrows(IdentityAlreadyExistsException.class, () -> this.identityService.create(this.requestDto));
        verify(this.repository, never()).save(any(IdentityDocument.class));
    }

    @Test
    @DisplayName("Should change password when credentials are valid")
    void changePasswordShouldChangePasswordWhenCredentialsAreValid() {
        // Given
        String username = "test-user";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newEncodedPassword = "newEncodedPassword";
        when(this.repository.findByUsername(username)).thenReturn(Optional.of(this.identityDocument));
        when(this.passwordEncoder.matches(oldPassword, this.identityDocument.getPassword())).thenReturn(true);
        when(this.passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

        // When
        this.identityService.changePassword(username, oldPassword, newPassword);

        // Assert
        verify(this.repository).save(any(IdentityDocument.class));
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when old password is wrong")
    void changePasswordShouldThrowInvalidCredentialsExceptionWhenOldPasswordIsWrong() {
        // Given
        String username = "test-user";
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword";
        when(this.repository.findByUsername(username)).thenReturn(Optional.of(this.identityDocument));
        when(this.passwordEncoder.matches(oldPassword, this.identityDocument.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            this.identityService.changePassword(username, oldPassword, newPassword);
        });
        verify(this.repository, never()).save(any(IdentityDocument.class));
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user does not exist")
    void changePasswordShouldThrowInvalidCredentialsExceptionWhenUserDoesNotExist() {
        // Given
        String username = "non-existent-user";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            this.identityService.changePassword(username, oldPassword, newPassword);
        });
        verify(this.repository, never()).save(any(IdentityDocument.class));
    }
}
