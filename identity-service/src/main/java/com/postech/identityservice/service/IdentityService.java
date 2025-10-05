package com.postech.identityservice.service;

import com.postech.identityservice.api.exception.IdentityAlreadyExistsException;
import com.postech.identityservice.api.exception.InvalidCredentialsException;
import com.postech.identityservice.data.document.IdentityDocument;
import com.postech.identityservice.data.repository.IIdentityRepository;
import com.postech.identityservice.dto.NewIdentityRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class IdentityService {
    private final PasswordEncoder passwordEncoder;
    private final IIdentityRepository repository;

    public UUID create(NewIdentityRequestDto requestDto) {
        Optional<IdentityDocument> identity = this.repository.findByUsername(requestDto.getUsername());
        if (identity.isPresent()) {
            throw new IdentityAlreadyExistsException(identity.get().getId());
        }
        return this.repository.save(this.toDocument(requestDto)).getId();
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Optional<IdentityDocument> identity = this.repository.findByUsername(username);
        if (identity.isEmpty() || !this.passwordEncoder.matches(oldPassword, identity.get().getPassword())) {
            throw new InvalidCredentialsException();
        }
        this.repository.save(identity.get().toBuilder().password(this.passwordEncoder.encode(newPassword)).build());
    }

    private IdentityDocument toDocument(NewIdentityRequestDto requestDto) {
        return IdentityDocument
                .builder()
                .role(requestDto.getRole())
                .username(requestDto.getUsername())
                .password(this.passwordEncoder.encode(requestDto.getPassword()))
                .authorities(Collections.emptySet())
                .build();
    }
}
