package com.nha.abdm.hip;

import com.nha.abdm.wrapper.client.api.LinkApi;
import com.nha.abdm.wrapper.client.api.PatientsApi;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
public class PatientController {

    private static final String requestId = "260ad625-ffb9-4c7d-b5bc-e099577e7e88";

    @GetMapping({"/patients/{patientId}"})
    public Patient fetchCareContextStatus(@PathVariable String abhaAddress) {

        // TODO: Logic to find patient in HIP database using abhaAddress.

        // Placeholder to send dummy patient.
        Patient patient = new Patient();
        patient.setAbhaAddress(abhaAddress);
        patient.setName("random");
        patient.setGender("M");
        patient.setDateOfBirth("1986-10-13");

        return patient;
    }

    @PostMapping({"/test-wrapper/upsert-patients"})
    public String upsertPatients() throws ApiException {
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

        return patientsApi.upsertPatients(patients);
    }

    @PostMapping({"/test-wrapper/link-carecontexts-demographics"})
    public String linkCareContextsDemographics() throws  ApiException {
        LinkApi linkApi = new LinkApi();

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

        return linkApi.linkCareContexts(linkCareContextsRequest);
    }

    @PostMapping({"/test-wrapper/link-carecontexts-mobile-otp"})
    public String linkCareContextsMobileOtp() throws  ApiException {
        LinkApi linkApi = new LinkApi();

        CareContext careContext1 = new CareContext();
        careContext1.setReferenceNumber("care-context-reference3");
        careContext1.setDisplay("care-context-display3");

        CareContext careContext2 = new CareContext();
        careContext2.setReferenceNumber("care-context-reference4");
        careContext2.setDisplay("care-context-display4");

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
        linkCareContextsRequest.setAuthMode(LinkCareContextsRequest.AuthModeEnum.MOBILE_OTP);
        linkCareContextsRequest.setPatient(patient);

        return linkApi.linkCareContexts(linkCareContextsRequest);
    }

    @PostMapping({"/test-wrapper/verify-otp"})
    public String verifyOtp(@RequestBody String otp) throws ApiException {
        LinkApi linkApi = new LinkApi();

        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
        verifyOTPRequest.setLoginHint(VerifyOTPRequest.LoginHintEnum.HIPLINKING);
        verifyOTPRequest.setRequestId(requestId);
        verifyOTPRequest.setAuthCode(otp);
        return linkApi.verifyOTP(verifyOTPRequest);
    }

    @GetMapping({"/test-wrapper/link-status"})
    public String linkStatus() throws ApiException, InterruptedException {
        LinkApi linkApi = new LinkApi();

        long NANOSEC_PER_SEC = 1000l*1000*1000;
        long startTime = System.nanoTime();
        // Run loop for 5 minutes.
        while ((System.nanoTime()-startTime) < 5*60*NANOSEC_PER_SEC) {
            // At an interval of 5 seconds.
            Thread.sleep(5000);
            // To make this periodic poll, requestId can be persisted to facility's / HIP's database.
            FacadeResponse facadeResponse = linkApi.linkStatusRequestIdGet(requestId);
            System.out.println(facadeResponse.getMessage());
            if ("SUCESSS".equals(facadeResponse.getMessage()) || "Success".equals(facadeResponse.getMessage()) || "success".equals(facadeResponse.getMessage())
            || "Some of the Care Contexts are already linked, please remove the linked Care Contexts.".equals(facadeResponse.getMessage())) {
                return "Care Contexts linked successfully";
            }
        }
        return "Request Timed Out";
    }
}
