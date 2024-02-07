/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers;

public class FieldIdentifiers {

  public static final String GATEWAY_REQUEST_ID = "gatewayRequestId";
  public static final String REQUEST_DETAILS = "requestDetails";
  public static final String STATUS = "status";
  public static final String ERROR = "error";
  public static final String PATIENT_ABHA_ADDRESS = "patientAbhaAddress";
  public static final String CONSENTS = "consents";
  public static final String RESPONSE_DETAILS = "responseDetails";
  public static final String CONSENT_ID = "consentId";

  // Nested fields.
  public static final String LINK_RECORDS_REQUEST = "linkRecordsRequest";
  public static final String HIP_ON_INIT_RESPONSE = "hipOnInitResponse";
  public static final String HIP_ON_ADD_CARE_CONTEXT_RESPONSE = "hipOnAddCareContext";
  public static final String CONSENT_REQUEST_ID = "consentRequestId";
  public static final String CONSENT_ON_INIT_RESPONSE = "consentOnInitResponse";
  public static final String CONSENT_ON_STATUS_RESPONSE = "consentOnStatusResponse";
  public static final String CONSENT_ON_NOTIFY_RESPONSE = "consentOnNotifyResponse";
  public static final String HIP_ON_CONFIRM_RESPONSE = "hipOnConfirmResponse";
  public static final String HIP_NOTIFY_REQUEST = "HIPConsentNotification";
  public static final String HEALTH_INFORMATION_REQUEST = "HIPHealthInformationRequest";
}
