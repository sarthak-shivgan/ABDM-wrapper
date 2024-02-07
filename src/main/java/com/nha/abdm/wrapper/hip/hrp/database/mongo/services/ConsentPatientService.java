/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.ConsentPatient;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ConsentPatientService {

  private final MongoTemplate mongoTemplate;

  @Autowired
  public ConsentPatientService(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public void saveConsentPatientMapping(String consentId, String patientAbhaAddress) {
    MongoCollection<Document> collection = mongoTemplate.getCollection("consent-patient");
    UpdateOptions updateOptions = new UpdateOptions().upsert(true);
    collection.updateOne(
        Filters.eq(FieldIdentifiers.CONSENT_ID, consentId),
        Updates.combine(Updates.set(FieldIdentifiers.PATIENT_ABHA_ADDRESS, patientAbhaAddress)),
        updateOptions);
  }

  public boolean findMappingByConsentId(String consentId) {
    Query query = new Query(Criteria.where(FieldIdentifiers.CONSENT_ID).is(consentId));
    ConsentPatient consentPatient = mongoTemplate.findOne(query, ConsentPatient.class);

    return consentPatient != null;
  }
}
