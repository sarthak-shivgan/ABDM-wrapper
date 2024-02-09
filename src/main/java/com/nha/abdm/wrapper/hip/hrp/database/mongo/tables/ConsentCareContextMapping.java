/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.tables;

import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentCareContexts;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "consent-careContexts")
public class ConsentCareContextMapping {
  @Field("consentId")
  @Indexed(unique = true)
  public String consentId;

  @Field("careContexts")
  public List<ConsentCareContexts> careContexts;
}
