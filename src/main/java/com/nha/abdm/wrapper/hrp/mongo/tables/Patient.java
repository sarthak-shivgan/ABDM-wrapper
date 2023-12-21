package com.nha.abdm.wrapper.hrp.mongo.tables;

//import com.nha.abdm.wrapper.hrp.CareContextService;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "patient")
public class Patient {

    @Field("abhaAddress")
    @Indexed(unique = true)
    public String abhaAddress;
    public Patient(String abhaAddress, String name, String gender, String dateOfBirth, String patientReference, String display, List<LinkRecordsResponse.CareContext> careContexts, String lastUpdated) {
        this.abhaAddress=abhaAddress;
        this.name=name;
        this.gender=gender;
        this.dateOfBirth=dateOfBirth;
        this.patientReference = patientReference;
        this.display=display;
        this.careContexts=careContexts;
        this.lastUpdated=lastUpdated;
    }
    public Patient(){

    }

    public String getAbhaAddress() {
        return abhaAddress;
    }

    public void setAbhaAddress(String abhaAddress) {
        this.abhaAddress = abhaAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPatientReference() {
        return patientReference;
    }

    public void setPatientReference(String patientReference) {
        this.patientReference = patientReference;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<LinkRecordsResponse.CareContext> getCareContexts() {
        return careContexts;
    }

    public void setCareContexts(List<LinkRecordsResponse.CareContext> careContexts) {
        this.careContexts.addAll(careContexts);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Field("name")
    public String name;

    @Field("gender")
    public String gender;

    @Field("dateOfBirth")
    public String dateOfBirth;

    @Field("patientReference")
    private String patientReference;

    @Field("patientDisplay")
    private String display;

    @Field("careContext")
    private List<LinkRecordsResponse.CareContext> careContexts;

    @Field("lastUpdated")
    private String lastUpdated;
}
