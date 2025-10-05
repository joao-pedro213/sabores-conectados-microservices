package com.postech.authorizationservice.service;

import com.postech.authorizationservice.data.document.IdentityDocument;
import com.postech.authorizationservice.data.repository.IIdentityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private IIdentityRepository repository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String username = "test-user";
        IdentityDocument identityDocument = IdentityDocument
                .builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("encodedPassword")
                .authorities(Set.of("api:read"))
                .build();
        when(this.repository.findByUsername(username)).thenReturn(Optional.of(identityDocument));

        // When
        IdentityPrincipal identityPrincipal = this.userDetailsServiceImpl.loadUserByUsername(username);

        // Assert
        assertThat(identityPrincipal).isNotNull();
        assertThat(identityPrincipal.getId()).isEqualTo(identityDocument.getId());
        assertThat(identityPrincipal.getUsername()).isEqualTo(identityDocument.getUsername());
        assertThat(identityPrincipal.getPassword()).isEqualTo(identityDocument.getPassword());
        assertThat(identityPrincipal.getAuthorities().iterator().next().getAuthority()).isEqualTo("api:read");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserIsNotFound() {
        // Arrange
        String username = "non-existent-user";
        when(this.repository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsServiceImpl.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Identity not Found");
    }
}
