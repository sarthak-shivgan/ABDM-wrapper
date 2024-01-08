package com.nha.abdm.wrapper.hrp.repository;

import com.nha.abdm.wrapper.hrp.mongo.tables.RequestLogs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepo extends MongoRepository<RequestLogs, String> {
    RequestLogs findByClientRequestId(String clientRequestId);
    RequestLogs findByTransactionId(String transactionId);
    @Query("{linkRefNumber :?0}")
    RequestLogs findByLinkRefNumber(String linkRefNumber);
    RequestLogs findByGatewayRequestId(String clientRequestId);
    RequestLogs findByRequestId(String requestId);


}
