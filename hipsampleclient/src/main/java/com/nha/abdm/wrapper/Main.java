package com.nha.abdm.wrapper;

import com.nha.abdm.wrapper.client.api.PatientsApi;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ApiException {

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

        patientsApi.upsertPatients(patients);
    }

}