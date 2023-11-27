//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ResponseController;

import java.io.Serializable;
import java.util.List;

public class LinkRecordsResponse implements Serializable {
	private String requesterId;
	private String abhaAddress;
	private String authMode;
	private String name;
	private String gender;
	private String dateOfBirth;
	private PatientData patient;

	public LinkRecordsResponse() {
	}

	public String getRequesterId() {
		return this.requesterId;
	}

	public void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}

	public String getAbhaAddress() {
		return this.abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public String getAuthMode() {
		return this.authMode;
	}

	public void setAuthMode(String authMode) {
		this.authMode = authMode;
	}

	public String getName() {
		return this.name;
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

	public PatientData getPatient() {
		return this.patient;
	}

	public void setPatient(PatientData patient) {
		this.patient = patient;
	}

	public static class PatientData {
		private String referenceNumber;
		private String display;
		private List<CareContext> careContexts;

		public PatientData() {
		}

		public String getReferenceNumber() {
			return this.referenceNumber;
		}

		public void setReferenceNumber(String referenceNumber) {
			this.referenceNumber = referenceNumber;
		}

		public String getDisplay() {
			return this.display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public List<CareContext> getCareContexts() {
			return this.careContexts;
		}

		public void setCareContexts(List<CareContext> careContexts) {
			this.careContexts = careContexts;
		}
	}

	public static class CareContext {
		private String referenceNumber;
		private String display;

		public CareContext() {
		}

		public String getReferenceNumber() {
			return this.referenceNumber;
		}

		public void setReferenceNumber(String referenceNumber) {
			this.referenceNumber = referenceNumber;
		}

		public String getDisplay() {
			return this.display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}
	}
}
