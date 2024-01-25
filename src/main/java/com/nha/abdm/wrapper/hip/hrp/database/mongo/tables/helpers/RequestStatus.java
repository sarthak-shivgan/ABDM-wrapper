/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers;

public enum RequestStatus {
  AUTH_INIT_ACCEPTED("HIP Initiated link auth init request accepted by gateway"),
  AUTH_INIT_ERROR("Error thrown by Gateway for HIP Initiated link auth init"),
  AUTH_CONFIRM_ACCEPTED("HIP Initiated link aut confirm request accepted by gateway"),
  ADD_CARE_CONTEXT_ACCEPTED("Add Care Context request accepted by gateway"),
  CARE_CONTEXT_LINKED("Care Context(s) were linked"),
  USER_INIT_REQUEST_RECEIVED_BY_WRAPPER(
      "User initiated link request received by wrapper from gateway"),
  ;

  private String value;

  RequestStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
