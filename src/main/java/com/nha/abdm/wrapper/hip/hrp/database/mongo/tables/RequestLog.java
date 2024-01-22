/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import java.util.HashMap;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "request-logs")
public class RequestLog {
  @Field("clientRequestId")
  public String clientRequestId;

  @Field("requestId")
  public String requestId;

  @Field("gatewayRequestId")
  public String gatewayRequestId;

  @Field("linkStatus")
  public String linkStatus;

  @Field("transactionId")
  public String transactionId;

  @Field("linkRefNumber")
  public String linkRefNumber;

  @Field("abhaAddress")
  public String abhaAddress;

  @Field("otp")
  public String otp;

  @Field("rawResponse")
  public HashMap<String, Object> rawResponse;

  public RequestLog(
      String clientRequestId, String gatewayRequestId, String abhaAddress, String transactionId) {
    this.clientRequestId = clientRequestId;
    this.gatewayRequestId = gatewayRequestId;
    this.abhaAddress = abhaAddress;
    this.transactionId = transactionId;
    this.linkStatus = "Initiated";
    this.rawResponse = new HashMap<>();
  }

  public RequestLog() {}
}
