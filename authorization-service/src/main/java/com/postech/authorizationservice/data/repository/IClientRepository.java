package com.postech.authorizationservice.data.repository;

import com.postech.authorizationservice.data.document.ClientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientRepository extends MongoRepository<ClientDocument, UUID> {
    Optional<ClientDocument> findByClientId(String clientId);
}
