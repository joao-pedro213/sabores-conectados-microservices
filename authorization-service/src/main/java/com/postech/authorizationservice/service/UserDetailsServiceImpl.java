package com.postech.authorizationservice.service;

import com.postech.authorizationservice.data.document.IdentityDocument;
import com.postech.authorizationservice.data.repository.IIdentityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final IIdentityRepository repository;

    @Override
    public IdentityPrincipal loadUserByUsername(String username) {
        IdentityDocument identity = this.repository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Identity not Found"));
        return IdentityPrincipal
                .builder()
                .id(identity.getId())
                .username(identity.getUsername())
                .password(identity.getPassword())
                .role(identity.getRole())
                .authorities(identity.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList())
                .build();
    }
}
