//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ServiceImpl;

import com.wrapper.Model.CareContextTable;
import com.wrapper.Repository.CareContextRepo;
import com.wrapper.ResponseController.LinkRecordsResponse;
import com.wrapper.ResponseController.OnConfirmResponse;
import com.wrapper.ResponseController.OnInitResponse;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CareContextTableService {
	private static final Logger log = LogManager.getLogger(CareContextTableService.class);
	@Autowired
	private final CareContextRepo careContextRepo;
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	public CareContextTableService(CareContextRepo careContextRepo) {
		this.careContextRepo = careContextRepo;
	}

	public CareContextTable findByOnInitRequestId(String initRequestId) {
		return this.careContextRepo.findByInitRequestId(initRequestId);
	}

	public CareContextTable findByConfirmRequestId(String confirmRequestId) {
		return this.careContextRepo.findByConfirmRequestId(confirmRequestId);
	}

	public CareContextTable findByCareContextRequestId(String careContextRequestId) {
		return this.careContextRepo.findByCareContextRequestId(careContextRequestId);
	}

	public CareContextTable setRequestId(String initRequestId, String name, String gender, String dateOfBirth, LinkRecordsResponse data) {
		CareContextTable existingRecord = this.careContextRepo.findByInitRequestId(initRequestId);
		String patientReferenceNumber = data.getPatient().getReferenceNumber();
		String patientDisplay = data.getPatient().getDisplay();
		List<LinkRecordsResponse.CareContext> careContexts = data.getPatient().getCareContexts();
		if (existingRecord == null) {
			CareContextTable newRecord = new CareContextTable(initRequestId, "", "", "", "", name, gender, dateOfBirth, patientReferenceNumber, patientDisplay, careContexts, "Initiated");
			return (CareContextTable)this.careContextRepo.save(newRecord);
		} else {
			return existingRecord;
		}
	}

	public String getName(String initRequestId) {
		Query query = new Query(Criteria.where("initRequestId").is(initRequestId));
		query.fields().include("name");
		CareContextTable result = (CareContextTable)this.mongoTemplate.findOne(query, CareContextTable.class);

		assert result != null;

		return result.getName();
	}

	@Transactional
	public String setTransactionId(OnInitResponse data) {
		String requestId = data.getResp().getRequestId();
		String transactionId = data.getAuth().getTransactionId();
		log.info(" in setTransaction RequestId: {} transactionId: {}", requestId, transactionId);
		Query query = new Query(Criteria.where("initRequestId").is(requestId));
		Update update = (new Update()).set("transactionId", transactionId);
		long modifiedCount = this.mongoTemplate.updateFirst(query, update, CareContextTable.class).getModifiedCount();
		return modifiedCount > 0L ? "Successfully updated TransactionId" : "Failed to update TransactionId. No record found with the given requestId.";
	}

	@Transactional
	public String setAccessToken(OnConfirmResponse data) {
		String requestId = data.getResp().getRequestId();
		String accessToken = data.getAuth().getAccessToken();
		log.info(" in setAccessToken RequestId: {} AccessToken: {}", requestId, accessToken);
		Query query = new Query(Criteria.where("confirmRequestId").is(requestId));
		Update update = (new Update()).set("accessToken", accessToken);
		long modifiedCount = this.mongoTemplate.updateFirst(query, update, CareContextTable.class).getModifiedCount();
		return modifiedCount > 0L ? "Successfully updated AccessToken" : "Failed to update AccessToken. No record found with the given requestId.";
	}

	@Transactional
	public String getTransactionId(String requestId) {
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getTransactionId() : null;
	}

	@Transactional
	public String getAccessToken(String requestId) {
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getAccessToken() : null;
	}

	public String getGender(String initRequestId) {
		Query query = new Query(Criteria.where("initRequestId").is(initRequestId));
		query.fields().include("gender");
		CareContextTable result = (CareContextTable)this.mongoTemplate.findOne(query, CareContextTable.class);

		assert result != null;

		return result.getGender();
	}

	public String getDateOfBirth(String initRequestId) {
		Query query = new Query(Criteria.where("initRequestId").is(initRequestId));
		query.fields().include("dateOfBirth");
		CareContextTable result = (CareContextTable)this.mongoTemplate.findOne(query, CareContextTable.class);

		assert result != null;

		return result.getDateOfBirth();
	}

	public String getReferenceNumber(String requestId) {
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getPatientReferenceNumber() : null;
	}

	public String getDisplay(String requestId) {
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getPatientDisplay() : null;
	}

	public List<LinkRecordsResponse.CareContext> getCareContext(String requestId) {
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getCareContexts() : Collections.emptyList();
	}

	public String setConfirmRequestId(String requestId, String confirmRequestId) {
		Query query = new Query(Criteria.where("initRequestId").is(requestId));
		Update update = (new Update()).set("confirmRequestId", confirmRequestId);
		long modifiedCount = this.mongoTemplate.updateFirst(query, update, CareContextTable.class).getModifiedCount();
		if (modifiedCount > 0L) {
			log.info("confirm updated successfully");
			return "Successfully updated AccessToken";
		} else {
			return "Failed to update AccessToken. No record found with the given requestId.";
		}
	}

	public String setCareContextRequestId(String requestId, String careContextRequestId) {
		this.careContextRepo.findByConfirmRequestId(requestId);
		Query query = new Query(Criteria.where("confirmRequestId").is(requestId));
		Update update = (new Update()).set("careContextRequestId", careContextRequestId);
		long modifiedCount = this.mongoTemplate.updateFirst(query, update, CareContextTable.class).getModifiedCount();
		return modifiedCount > 0L ? "Successfully updated AccessToken" : "Failed to update AccessToken. No record found with the given requestId.";
	}

	public String getPatientReferenceNumber(String requestId) {
		log.info("IN getPatientReferenceNumber  requestID :" + requestId);
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getPatientReferenceNumber() : null;
	}

	public String getPatientDisplay(String requestId) {
		log.info("IN getPatientDisplay requestID :" + requestId);
		CareContextTable careContextTable = this.careContextRepo.findByConfirmRequestId(requestId);
		return careContextTable != null ? careContextTable.getPatientDisplay() : null;
	}

	public void setStatus(String careContextRequestId, String status) {
		Query query = new Query(Criteria.where("careContextRequestId").is(careContextRequestId));
		Update update = (new Update()).set("status", status);
		long modifiedCount = this.mongoTemplate.updateFirst(query, update, CareContextTable.class).getModifiedCount();
	}

	public String getStatus(String careContextRequestId) {
		CareContextTable careContextTable = this.careContextRepo.findByInitRequestId(careContextRequestId);
		return careContextTable != null ? careContextTable.getStatus() : "Not Found";
	}
}
