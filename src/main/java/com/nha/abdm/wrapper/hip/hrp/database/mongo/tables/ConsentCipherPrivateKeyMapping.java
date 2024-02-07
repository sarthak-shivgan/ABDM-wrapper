/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "consent-key-mappings")
public class ConsentCipherPrivateKeyMapping {

  @Field(FieldIdentifiers.CONSENT_REQUEST_ID)
  @Indexed(unique = true)
  public String consentRequestId;

  @Field(FieldIdentifiers.PRIVATE_KEY)
  public String privateKey;
}
