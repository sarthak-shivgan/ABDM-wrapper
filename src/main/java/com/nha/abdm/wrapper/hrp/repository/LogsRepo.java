package com.nha.abdm.wrapper.hrp.repository;

import com.nha.abdm.wrapper.hrp.mongo.tables.LogsTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepo extends MongoRepository<LogsTable, String> {
    LogsTable findByClientRequestId(String clientRequestId);
    LogsTable findByTransactionId(String transactionId);
    @Query("{linkRefNumber :?0}")
    LogsTable findByLinkRefNumber(String linkRefNumber);
    LogsTable findByGatewayRequestId(String clientRequestId);

}
