package com.postech.identityservice.data.document;

import com.postech.identityservice.data.document.enumerator.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document("identities")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocument {
    @Id
    private UUID id;
    private String username;
    private String password;
    private SystemRole role;
    private Set<String> authorities;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
