package com.postech.authorizationservice.data.repository;

import com.postech.authorizationservice.data.document.IdentityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIdentityRepository extends MongoRepository<IdentityDocument, UUID> {
    Optional<IdentityDocument> findByUsername(String username);
}
