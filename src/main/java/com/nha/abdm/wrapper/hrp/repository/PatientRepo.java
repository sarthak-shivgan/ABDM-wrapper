package com.nha.abdm.wrapper.hrp.repository;

import com.nha.abdm.wrapper.hrp.mongo.tables.PatientTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepo extends MongoRepository<PatientTable, String> {
    PatientTable findByAbhaAddress(String abhaAddress);
}