package com.postech.core.account.domain.entity;

import com.postech.core.account.domain.exception.InvalidEmailException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
public class AccountEntity {
    private UUID id;
    private UUID identityId;
    private String name;
    private String email;
    private String address;
    private LocalDateTime lastUpdated;

    private AccountEntity(
            UUID id, UUID identityId, String name,
            String email, String address, LocalDateTime lastUpdated) {
        this.id = id;
        this.identityId = identityId;
        this.name = name;
        this.setEmail(email);
        this.address = address;
        this.lastUpdated = lastUpdated;
    }

    public void setEmail(String email) {
        if (!this.isEmailValid(email)) {
            throw new InvalidEmailException();
        }
        this.email = email;
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        return EmailValidator.getInstance().isValid(email);
    }

    public static class AccountEntityBuilder {
        public AccountEntity build() {
            return new AccountEntity(this.id, this.identityId, this.name, this.email, this.address, this.lastUpdated);
        }
    }
}
