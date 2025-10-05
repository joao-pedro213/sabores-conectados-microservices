package com.postech.accountservice.data.repository;

import com.postech.accountservice.data.document.AccountDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAccountRepository extends CrudRepository<AccountDocument, UUID> {
    Optional<AccountDocument> findByIdentityId(UUID identityId);
}
