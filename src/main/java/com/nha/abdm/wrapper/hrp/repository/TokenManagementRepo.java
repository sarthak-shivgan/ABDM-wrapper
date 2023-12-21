package com.nha.abdm.wrapper.hrp.repository;

import com.nha.abdm.wrapper.hrp.mongo.tables.Patient;
import com.nha.abdm.wrapper.hrp.mongo.tables.TokenManagement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenManagementRepo extends MongoRepository<TokenManagement, String> {
    TokenManagement findByAbhaAddress(String abhaAddress);
}
