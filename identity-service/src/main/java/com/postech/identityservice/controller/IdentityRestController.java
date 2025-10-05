package com.postech.identityservice.controller;

import com.postech.identityservice.dto.ChangePasswordRequestDto;
import com.postech.identityservice.dto.NewIdentityRequestDto;
import com.postech.identityservice.service.IdentityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class IdentityRestController {
    private final IdentityService service;

    @PreAuthorize("hasAuthority('SCOPE_identity:write')")
    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody NewIdentityRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(requestDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto requestDto) {
        this.service.changePassword(requestDto.getUsername(), requestDto.getOldPassword(), requestDto.getNewPassword());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
