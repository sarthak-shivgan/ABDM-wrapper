/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories;

import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepo extends MongoRepository<Patient, String> {
  Patient findByAbhaAddress(String abhaAddress);

  Patient findByPatientReference(String patientReference);

  List<Patient> findByPatientMobile(String patientMobile);
}
