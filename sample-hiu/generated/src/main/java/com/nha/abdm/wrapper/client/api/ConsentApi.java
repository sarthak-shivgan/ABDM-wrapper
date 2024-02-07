/*
 * Swagger HIU Facade - OpenAPI 3.0
 * This is a set of interfaces based on the OpenAPI 3.0 specification for a wrapper client
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.nha.abdm.wrapper.client.api;

import com.nha.abdm.wrapper.client.invoker.ApiCallback;
import com.nha.abdm.wrapper.client.invoker.ApiClient;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.invoker.ApiResponse;
import com.nha.abdm.wrapper.client.invoker.Configuration;
import com.nha.abdm.wrapper.client.invoker.Pair;
import com.nha.abdm.wrapper.client.invoker.ProgressRequestBody;
import com.nha.abdm.wrapper.client.invoker.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import com.nha.abdm.wrapper.client.model.ConsentResponse;
import com.nha.abdm.wrapper.client.model.ConsentStatusResponse;
import com.nha.abdm.wrapper.client.model.FacadeResponse;
import com.nha.abdm.wrapper.client.model.FetchPatientConsentRequest;
import com.nha.abdm.wrapper.client.model.InitConsentRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.GenericType;

public class ConsentApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public ConsentApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ConsentApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public int getHostIndex() {
        return localHostIndex;
    }

    public void setHostIndex(int hostIndex) {
        this.localHostIndex = hostIndex;
    }

    public String getCustomBaseUrl() {
        return localCustomBaseUrl;
    }

    public void setCustomBaseUrl(String customBaseUrl) {
        this.localCustomBaseUrl = customBaseUrl;
    }

    /**
     * Build call for consentStatusRequestIdGet
     * @param requestId Request Id of the consent request. (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call consentStatusRequestIdGetCall(String requestId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/consent-status/{requestId}"
            .replace("{" + "requestId" + "}", localVarApiClient.escapeString(requestId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call consentStatusRequestIdGetValidateBeforeCall(String requestId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'requestId' is set
        if (requestId == null) {
            throw new ApiException("Missing the required parameter 'requestId' when calling consentStatusRequestIdGet(Async)");
        }

        return consentStatusRequestIdGetCall(requestId, _callback);

    }

    /**
     * Get status of Consent request.
     * 
     * @param requestId Request Id of the consent request. (required)
     * @return ConsentStatusResponse
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public ConsentStatusResponse consentStatusRequestIdGet(String requestId) throws ApiException {
        ApiResponse<ConsentStatusResponse> localVarResp = consentStatusRequestIdGetWithHttpInfo(requestId);
        return localVarResp.getData();
    }

    /**
     * Get status of Consent request.
     * 
     * @param requestId Request Id of the consent request. (required)
     * @return ApiResponse&lt;ConsentStatusResponse&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<ConsentStatusResponse> consentStatusRequestIdGetWithHttpInfo(String requestId) throws ApiException {
        okhttp3.Call localVarCall = consentStatusRequestIdGetValidateBeforeCall(requestId, null);
        Type localVarReturnType = new TypeToken<ConsentStatusResponse>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get status of Consent request. (asynchronously)
     * 
     * @param requestId Request Id of the consent request. (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call consentStatusRequestIdGetAsync(String requestId, final ApiCallback<ConsentStatusResponse> _callback) throws ApiException {

        okhttp3.Call localVarCall = consentStatusRequestIdGetValidateBeforeCall(requestId, _callback);
        Type localVarReturnType = new TypeToken<ConsentStatusResponse>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for fetchConsent
     * @param fetchPatientConsentRequest Request body for fetch consent request (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call fetchConsentCall(FetchPatientConsentRequest fetchPatientConsentRequest, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = fetchPatientConsentRequest;

        // create path and map variables
        String localVarPath = "/fetch-consent";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call fetchConsentValidateBeforeCall(FetchPatientConsentRequest fetchPatientConsentRequest, final ApiCallback _callback) throws ApiException {
        return fetchConsentCall(fetchPatientConsentRequest, _callback);

    }

    /**
     * Fetches consent details
     * Fetches consent details
     * @param fetchPatientConsentRequest Request body for fetch consent request (optional)
     * @return ConsentResponse
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public ConsentResponse fetchConsent(FetchPatientConsentRequest fetchPatientConsentRequest) throws ApiException {
        ApiResponse<ConsentResponse> localVarResp = fetchConsentWithHttpInfo(fetchPatientConsentRequest);
        return localVarResp.getData();
    }

    /**
     * Fetches consent details
     * Fetches consent details
     * @param fetchPatientConsentRequest Request body for fetch consent request (optional)
     * @return ApiResponse&lt;ConsentResponse&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<ConsentResponse> fetchConsentWithHttpInfo(FetchPatientConsentRequest fetchPatientConsentRequest) throws ApiException {
        okhttp3.Call localVarCall = fetchConsentValidateBeforeCall(fetchPatientConsentRequest, null);
        Type localVarReturnType = new TypeToken<ConsentResponse>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Fetches consent details (asynchronously)
     * Fetches consent details
     * @param fetchPatientConsentRequest Request body for fetch consent request (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call fetchConsentAsync(FetchPatientConsentRequest fetchPatientConsentRequest, final ApiCallback<ConsentResponse> _callback) throws ApiException {

        okhttp3.Call localVarCall = fetchConsentValidateBeforeCall(fetchPatientConsentRequest, _callback);
        Type localVarReturnType = new TypeToken<ConsentResponse>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for initConsent
     * @param initConsentRequest Request body for initiate consent request (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 202 </td><td> Request accepted </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call initConsentCall(InitConsentRequest initConsentRequest, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = initConsentRequest;

        // create path and map variables
        String localVarPath = "/consent-init";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call initConsentValidateBeforeCall(InitConsentRequest initConsentRequest, final ApiCallback _callback) throws ApiException {
        return initConsentCall(initConsentRequest, _callback);

    }

    /**
     * Initiates consent request
     * Initiates consent request
     * @param initConsentRequest Request body for initiate consent request (optional)
     * @return FacadeResponse
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 202 </td><td> Request accepted </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public FacadeResponse initConsent(InitConsentRequest initConsentRequest) throws ApiException {
        ApiResponse<FacadeResponse> localVarResp = initConsentWithHttpInfo(initConsentRequest);
        return localVarResp.getData();
    }

    /**
     * Initiates consent request
     * Initiates consent request
     * @param initConsentRequest Request body for initiate consent request (optional)
     * @return ApiResponse&lt;FacadeResponse&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 202 </td><td> Request accepted </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<FacadeResponse> initConsentWithHttpInfo(InitConsentRequest initConsentRequest) throws ApiException {
        okhttp3.Call localVarCall = initConsentValidateBeforeCall(initConsentRequest, null);
        Type localVarReturnType = new TypeToken<FacadeResponse>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Initiates consent request (asynchronously)
     * Initiates consent request
     * @param initConsentRequest Request body for initiate consent request (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
        <tr><td> 202 </td><td> Request accepted </td><td>  -  </td></tr>
        <tr><td> 400 </td><td> Invalid request body supplied </td><td>  -  </td></tr>
        <tr><td> 404 </td><td> Address not found </td><td>  -  </td></tr>
        <tr><td> 422 </td><td> Validation exception </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call initConsentAsync(InitConsentRequest initConsentRequest, final ApiCallback<FacadeResponse> _callback) throws ApiException {

        okhttp3.Call localVarCall = initConsentValidateBeforeCall(initConsentRequest, _callback);
        Type localVarReturnType = new TypeToken<FacadeResponse>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
