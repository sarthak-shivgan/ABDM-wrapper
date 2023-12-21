package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.AddPatient;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Patient;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Document(collection = "patient")
@Service
public class PatientTableService {
    private static final Logger log = LogManager.getLogger(PatientTableService.class);
    @Autowired
    private final PatientRepo patientRepo;
    @Autowired
    Utils utils;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    LinkRecordsResponse linkRecordsResponse;

    @Autowired
    public PatientTableService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Transactional
    public String addPatient(Object content) {

        if(content.getClass()== LinkRecordsResponse.class){
            LinkRecordsResponse data=(LinkRecordsResponse) content;
            String patientReference=data.getPatientReference();
            try{
                Patient existingRecord = this.patientRepo.findByPatientReference(patientReference);
//            List<LinkRecordsResponse.CareContext> careContexts = data.getPatient().getCareContexts();
                if (existingRecord == null) {
                    return null;
                } else {
                    Query query = new Query(Criteria.where("patientReference").is(data.getPatientReference()));
                    Update update = new Update().addToSet("careContext").each(data.getPatient().getCareContexts()) ;//TODO
                    this.mongoTemplate.updateFirst(query, update, Patient.class);
                }
            }catch(Exception e){
                log.info("addPatient :"+e);
            }
        }else if(content.getClass()== AddPatient.class){
            AddPatient data=(AddPatient)content;
            Patient existingRecord=patientRepo.findByAbhaAddress(data.getAbhaAddress());
            if(existingRecord==null){
                Patient newRecord=new Patient();
                newRecord.setName(data.getName());
                newRecord.setAbhaAddress(data.getAbhaAddress());
                newRecord.setPatientReference(data.getPatientReference());
                newRecord.setGender(data.getGender());
                newRecord.setDateOfBirth(data.getDateOfBirth());
                newRecord.setDisplay(data.getDisplay());
                patientRepo.save(newRecord);
                return  "Successfully Added Patient";
            }else{
                Update update = new Update().set("abhaAddress",data.getAbhaAddress())
                        .set("name",data.getName())
                        .set("gender",data.getGender())
                        .set("dateOfBirth",data.getDateOfBirth())
                        .set("display",data.getDisplay())
                        .set("patientReference",data.getPatientReference());
                Query query = new Query(Criteria.where("abhaAddress").is(data.getAbhaAddress()));
                mongoTemplate.updateFirst(query, update, Patient.class);
                return  "Successfully Updated Patient";
            }

        }
        return null;




    }

    public List<LinkRecordsResponse.CareContext> getCareContexts(String abhaAddress) {
        Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getCareContexts() : null;
    }

    public String getPatientReference(String abhaAddress) {
        Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getPatientReference() : "";
    }

    public String getPatientDisplay(String abhaAddress) {
        Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getDisplay() : "";
    }
    public String getAbhaAddress(String patientReference){
        Patient existingRecord = this.patientRepo.findByPatientReference(patientReference);
        return existingRecord != null ? existingRecord.getAbhaAddress() : "";
    }
}
