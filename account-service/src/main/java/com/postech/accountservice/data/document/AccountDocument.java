package com.postech.accountservice.data.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDocument {
    @Id
    private UUID id;
    private UUID identityId;
    private String name;
    private String email;
    private String address;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
