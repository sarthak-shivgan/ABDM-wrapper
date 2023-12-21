package com.nha.abdm.wrapper.hrp.repository;

import com.nha.abdm.wrapper.hrp.mongo.tables.Logs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepo extends MongoRepository<Logs, String> {
    Logs findByClientRequestId(String clientRequestId);
    Logs findByTransactionId(String transactionId);
    @Query("{linkRefNumber :?0}")
    Logs findByLinkRefNumber(String linkRefNumber);
    Logs findByGatewayRequestId(String clientRequestId);
    Logs findByRequestId(String requestId);


}
