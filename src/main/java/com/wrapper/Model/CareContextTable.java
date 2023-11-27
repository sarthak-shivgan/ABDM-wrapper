//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Model;

import com.wrapper.ResponseController.LinkRecordsResponse;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(
		collection = "careContexts"
)
public class CareContextTable {
	@Field("initRequestId")
	@Indexed(
			unique = true
	)
	public String initRequestId;
	@Field("confirmRequestId")
	@Indexed(
			unique = true
	)
	public String confirmRequestId;
	@Field("careContextRequestId")
	@Indexed(
			unique = true
	)
	public String careContextRequestId;
	@Field("transactionId")
	public String transactionId;
	@Field("accessToken")
	public String accessToken;
	@Field("name")
	public String name;
	@Field("gender")
	public String gender;
	@Field("dateOfBirth")
	public String dateOfBirth;
	@Field("patientReferenceNumber")
	private String referenceNumber;
	@Field("patientDisplay")
	private String display;
	@Field("status")
	private String status;
	@Field("careContext")
	private List<LinkRecordsResponse.CareContext> careContexts;

	public String getInitRequestId() {
		return this.initRequestId;
	}

	public void setInitRequestId(String initRequestId) {
		this.initRequestId = initRequestId;
	}

	public String getConfirmRequestId() {
		return this.confirmRequestId;
	}

	public void setConfirmRequestId(String confirmRequestId) {
		this.confirmRequestId = confirmRequestId;
	}

	public String getCareContextRequestId() {
		return this.careContextRequestId;
	}

	public void setCareContextRequestId(String careContextRequestId) {
		this.careContextRequestId = careContextRequestId;
	}

	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPatientReferenceNumber() {
		return this.referenceNumber;
	}

	public void setPatientReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getPatientDisplay() {
		return this.display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public List<LinkRecordsResponse.CareContext> getCareContexts() {
		return this.careContexts;
	}

	public void setCareContexts(List<LinkRecordsResponse.CareContext> careContexts) {
		this.careContexts = careContexts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public CareContextTable(String initRequestId, String confirmRequestId, String careContextRequestId, String transactionId, String accessToken, String name, String gender, String dateOfBirth, String referenceNumber, String display, List<LinkRecordsResponse.CareContext> careContexts, String status) {
		this.initRequestId = initRequestId;
		this.confirmRequestId = confirmRequestId;
		this.careContextRequestId = careContextRequestId;
		this.transactionId = transactionId;
		this.accessToken = accessToken;
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.referenceNumber = referenceNumber;
		this.display = display;
		this.careContexts = careContexts;
		this.status = status;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
