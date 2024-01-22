# PatientsApi

All URIs are relative to *http://localhost:8080/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**upsertPatients**](PatientsApi.md#upsertPatients) | **PUT** /add-patients | Insert or Update a list of patients |


<a id="upsertPatients"></a>
# **upsertPatients**
> String upsertPatients(patient)

Insert or Update a list of patients

Insert or Update a list of patients

### Example
```java
// Import classes:
import com.nha.abdm.wrapper.client.invoker.ApiClient;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.invoker.Configuration;
import com.nha.abdm.wrapper.client.invoker.models.*;
import com.nha.abdm.wrapper.client.api.PatientsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/v1");

    PatientsApi apiInstance = new PatientsApi(defaultClient);
    List<Patient> patient = Arrays.asList(); // List<Patient> | Insert or update a list of patients in the wrapper database
    try {
      String result = apiInstance.upsertPatients(patient);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PatientsApi#upsertPatients");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **patient** | [**List&lt;Patient&gt;**](Patient.md)| Insert or update a list of patients in the wrapper database | |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successfully added patients |  -  |
| **400** | Invalid ID supplied |  -  |
| **404** | Patient not found |  -  |
| **422** | Validation exception |  -  |

