package com.nha.abdm.wrapper.hrp.discoveryLinking.responses.helpers;

import lombok.Data;

import java.util.List;

@Data
public class PatientDemographicDetails {
    public String name;

    public  String gender;

    public String id;

    public String yearOfBirth;
    public List<PatientVerifiedIdentifiers> verifiedIdentifiers;
    public List<PatientUnVerifiedIdentifiers> unverifiedIdentifiers;
}