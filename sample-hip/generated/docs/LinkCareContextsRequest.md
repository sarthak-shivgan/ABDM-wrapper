

# LinkCareContextsRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** |  |  [optional] |
|**requesterId** | **String** |  |  [optional] |
|**abhaAddress** | **String** |  |  [optional] |
|**authMode** | [**AuthModeEnum**](#AuthModeEnum) |  |  [optional] |
|**patient** | [**PatientWithCareContext**](PatientWithCareContext.md) |  |  [optional] |



## Enum: AuthModeEnum

| Name | Value |
|---- | -----|
| DEMOGRAPHICS | &quot;DEMOGRAPHICS&quot; |
| MOBILE_OTP | &quot;MOBILE_OTP&quot; |
| AADHAAR_OTP | &quot;AADHAAR_OTP&quot; |



