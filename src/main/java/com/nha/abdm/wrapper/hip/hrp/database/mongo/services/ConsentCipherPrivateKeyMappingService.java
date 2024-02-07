/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsentCipherPrivateKeyMappingService {
  private final MongoTemplate mongoTemplate;

  @Autowired
  public ConsentCipherPrivateKeyMappingService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public void saveConsentPrivateKeyMapping(String consentRequestId, String privateKey) {
    MongoCollection<Document> collection = mongoTemplate.getCollection("consent-key-mappings");
    UpdateOptions updateOptions = new UpdateOptions().upsert(true);
    collection.updateOne(
        Filters.eq(FieldIdentifiers.CONSENT_REQUEST_ID, consentRequestId),
        Updates.combine(Updates.set(FieldIdentifiers.PRIVATE_KEY, privateKey)),
        updateOptions);
  }
}
