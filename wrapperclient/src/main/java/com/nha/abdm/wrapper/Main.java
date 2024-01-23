package com.nha.abdm.wrapper;

import com.nha.abdm.wrapper.client.api.LinkApi;
import com.nha.abdm.wrapper.client.api.PatientsApi;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.model.CareContext;
import com.nha.abdm.wrapper.client.model.FacadeResponse;
import com.nha.abdm.wrapper.client.model.LinkCareContextsRequest;
import com.nha.abdm.wrapper.client.model.Patient;
import com.nha.abdm.wrapper.client.model.PatientWithCareContext;
import com.nha.abdm.wrapper.client.model.VerifyOTPRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {

    private static final String requestId = "260ad625-ffb9-4c7d-b5bc-e099577e7e87";
    public static void main(String[] args) throws ApiException, InterruptedException {
        /**
         * Uncomment below to test addition or modification of patients.
         */
        // addOrModifyPatients();

        LinkApi linkApi = new LinkApi();
        /**
         * Uncomment below to test linking of care-contexts.
         */
        // linkCareContextRequest(linkApi, requestId);

        /**
         * Uncomment below to test linking of care-contexts.
         */
        // This is just an illustraion. Verify OTP should be called once HIP gets OTP from patient.
        // verifyOtp(linkApi, requestId, 123456);

        /**
         * Uncomment below to test status of link request.
         */
        /**
        long NANOSEC_PER_SEC = 1000l*1000*1000;

        long startTime = System.nanoTime();
        // Run loop for 5 minutes.
        while ((System.nanoTime()-startTime) < 5*60*NANOSEC_PER_SEC) {
            // At an interval of 5 seconds.
            Thread.sleep(5000);
            // To make this periodic poll, requestId can be persisted to facility's / HIP's database.
            FacadeResponse facadeResponse = linkApi.linkStatusRequestIdGet(requestId);
            if ("SUCESSS".equals(facadeResponse.getMessage()) || "Success".equals(facadeResponse.getMessage()) || "success".equals(facadeResponse.getMessage())) {
                System.out.println("Care Contexts linked successfully");
                break;
            }
        }*/
    }

    private static void addOrModifyPatients() throws ApiException{
        PatientsApi patientsApi = new PatientsApi();

        List<Patient> patients = new ArrayList<>();

        Patient patient1 = new Patient();
        patient1.setAbhaAddress("abc@sbx");
        patient1.setName("random");
        patient1.setGender("M");

        Patient patient2 = new Patient();
        patient2.setAbhaAddress("abcd@sbx");
        patient2.setName("kalyan");
        patient2.setGender("M");

        patients.add(patient1);
        patients.add(patient2);

        String response = patientsApi.upsertPatients(patients);
        System.out.println("Add Patients Response: " + response);
    }

    private static void linkCareContextRequest(LinkApi linkApi, String requestId) throws ApiException {

        CareContext careContext1 = new CareContext();
        careContext1.setReferenceNumber("care-context-reference1");
        careContext1.setDisplay("care-context-display1");

        CareContext careContext2 = new CareContext();
        careContext2.setReferenceNumber("care-context-reference2");
        careContext2.setDisplay("care-context-display2");

        List<CareContext> careContexts = new ArrayList<>();
        careContexts.add(careContext1);
        careContexts.add(careContext2);

        PatientWithCareContext patient = new PatientWithCareContext();
        patient.setId("atul_kumar13@sbx");
        patient.setReferenceNumber("patient123");
        patient.setCareContexts(careContexts);

        LinkCareContextsRequest linkCareContextsRequest = new LinkCareContextsRequest();
        linkCareContextsRequest.setRequestId(requestId);
        linkCareContextsRequest.setRequesterId("Demo_Atul_HIP");
        linkCareContextsRequest.setAbhaAddress("atul_kumar13@sbx");
        linkCareContextsRequest.setAuthMode(LinkCareContextsRequest.AuthModeEnum.DEMOGRAPHICS);
        linkCareContextsRequest.setPatient(patient);

        linkApi.linkCareContexts(linkCareContextsRequest);
    }

    private static void verifyOtp(LinkApi linkApi, String requestId, String otp) throws ApiException {

        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
        verifyOTPRequest.setLoginHint(VerifyOTPRequest.LoginHintEnum.HIPLINKING);
        verifyOTPRequest.setRequestId(requestId);
        verifyOTPRequest.setAuthCode(otp);
        linkApi.verifyOTP(verifyOTPRequest);
    }

}