package com.postech.authorizationservice.data.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document("clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocument {
    @Id
    private UUID id;
    private String name;
    private String clientId;
    private String clientSecret;
    private boolean isPublicClient;
    @Builder.Default
    private Set<String> redirectUris = new HashSet<>();
    @Builder.Default
    private Set<String> scopes = new HashSet<>();
}
