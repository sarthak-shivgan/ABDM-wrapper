/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories;

import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepo extends MongoRepository<RequestLog, String> {
  RequestLog findByClientRequestId(String clientRequestId);

  RequestLog findByTransactionId(String transactionId);

  @Query("{linkRefNumber :?0}")
  RequestLog findByLinkRefNumber(String linkRefNumber);

  RequestLog findByGatewayRequestId(String clientRequestId);

  RequestLog findByRequestId(String requestId);
}
