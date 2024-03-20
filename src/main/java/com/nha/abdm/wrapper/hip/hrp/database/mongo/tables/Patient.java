/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.common.models.Consent;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "patients")
public class Patient {

  @Field(FieldIdentifiers.ABHA_ADDRESS)
  @Indexed(unique = true)
  public String abhaAddress;

  @Field(FieldIdentifiers.NAME)
  public String name;

  @Field(FieldIdentifiers.GENDER)
  public String gender;

  @Field(FieldIdentifiers.DATE_OF_BIRTH)
  public String dateOfBirth;

  @Field(FieldIdentifiers.PATIENT_REFERENCE)
  @Indexed(unique = true)
  public String patientReference;

  @Field(FieldIdentifiers.PATIENT_DISPLAY)
  public String patientDisplay;

  @Field(FieldIdentifiers.PATIENT_MOBILE)
  public String patientMobile;

  @Field(FieldIdentifiers.CARE_CONTEXTS)
  public List<CareContext> careContexts;

  @Field(FieldIdentifiers.CONSENTS)
  public List<Consent> consents;

  public Patient() {}
}
