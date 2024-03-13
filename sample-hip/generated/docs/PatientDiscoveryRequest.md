

# PatientDiscoveryRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **UUID** | a nonce, unique for each HTTP request. |  |
|**timestamp** | **OffsetDateTime** | Date time format in UTC, includes miliseconds YYYY-MM-DDThh:mm:ss.vZ |  |
|**transactionId** | **UUID** | correlation-Id for patient discovery and subsequent care context linkage |  |
|**patient** | [**PatientDiscoveryRequestPatient**](PatientDiscoveryRequestPatient.md) |  |  |
|**hipId** | **String** |  |  [optional] |



