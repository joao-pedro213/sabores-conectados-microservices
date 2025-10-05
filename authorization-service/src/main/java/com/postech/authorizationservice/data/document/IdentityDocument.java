package com.postech.authorizationservice.data.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document("identities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocument {
    @Id
    private UUID id;
    private String username;
    private String password;
    private String role;
    private Set<String> authorities;
}
