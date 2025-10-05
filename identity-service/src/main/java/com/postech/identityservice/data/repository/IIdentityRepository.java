package com.postech.identityservice.data.repository;

import com.postech.identityservice.data.document.IdentityDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIdentityRepository extends CrudRepository<IdentityDocument, UUID> {
    Optional<IdentityDocument> findByUsername(String username);
}
