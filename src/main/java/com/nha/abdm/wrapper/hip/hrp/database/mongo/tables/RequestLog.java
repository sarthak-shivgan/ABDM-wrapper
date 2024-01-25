/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import java.util.HashMap;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "request-logs")
public class RequestLog {
  @Field("clientRequestId")
  public String clientRequestId;

  @Field("gatewayRequestId")
  public String gatewayRequestId;

  @Field("transactionId")
  public String transactionId;

  @Field("linkRefNumber")
  public String linkRefNumber;

  @Field("abhaAddress")
  public String abhaAddress;

  @Field("status")
  public RequestStatus status;

  @Field("error")
  public String error;

  @Field("otp")
  public String otp;

  @Field("requestDetails")
  public HashMap<String, Object> requestDetails;

  public RequestLog(
      String clientRequestId,
      String gatewayRequestId,
      String abhaAddress,
      String transactionId,
      RequestStatus status) {
    this.clientRequestId = clientRequestId;
    this.gatewayRequestId = gatewayRequestId;
    this.abhaAddress = abhaAddress;
    this.transactionId = transactionId;
    this.status = status;
    this.requestDetails = new HashMap<>();
  }

  public RequestLog() {}
}
