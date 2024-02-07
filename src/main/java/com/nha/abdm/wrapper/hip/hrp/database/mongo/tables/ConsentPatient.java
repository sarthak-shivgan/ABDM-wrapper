/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "consent-patient")
public class ConsentPatient {
  @Field("consentId")
  @Indexed(unique = true)
  public String consentId;

  @Field("patientAbhaAddress")
  public String patientAbhaAddress;
}
