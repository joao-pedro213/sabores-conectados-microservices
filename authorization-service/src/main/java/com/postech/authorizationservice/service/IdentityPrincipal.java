package com.postech.authorizationservice.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityPrincipal implements UserDetails {
    private UUID id;
    private String username;
    private String password;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;
}
