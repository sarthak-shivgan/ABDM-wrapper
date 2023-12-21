package com.nha.abdm.wrapper.hrp.discoveryLinking.responses;

import java.io.Serializable;

public class DiscoverResponse implements Serializable {
	public String requestId;

	public String transactionId;

	public String timestamp;
	public ErrorData error;
	public ErrorData getError() {
		return error;
	}

	public void setError(ErrorData error) {
		this.error = error;
	}



	public class ErrorData{
		public String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String message;
	}


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient patient;

	public String printData() {
		return "RequestID:"+getRequestId()+" transactionId :"+getTransactionId()+" name : "+getPatient().getName()+" gender : "+getPatient().getGender()+" id : "+getPatient().getId()+" yearOfBirth : "+getPatient().getYearOfBirth();
	}

	public class Patient{
		public String name;
		public  String gender;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getYearOfBirth() {
			return yearOfBirth;
		}

		public void setYearOfBirth(String yearOfBirth) {
			this.yearOfBirth = yearOfBirth;
		}

		public String id;

		public String yearOfBirth;


	}

}
