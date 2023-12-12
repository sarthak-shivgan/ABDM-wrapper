package com.nha.abdm.wrapper.hrp.discoveryLinking.responses;

import java.util.List;

public class InitResponse {

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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    private String requestId;
    private String transactionId;
    private Patient patient;


    public String printData() {
        return "RequestID:" + getRequestId() + " transactionId: " + getTransactionId() + " id: " +
                getPatient().getId() + " referenceNumber: " + getPatient().getReferenceNumber() +
                " CareContexts: " + getPatient().getCareContexts();
    }

    public class Patient {

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public List<InitCareContext> getCareContexts() {
            return careContexts;
        }

        public void setCareContexts(List<InitCareContext> careContexts) {
            this.careContexts = careContexts;
        }

        private String id;
        private String referenceNumber;
        private List<InitCareContext> careContexts;


        public static class InitCareContext {

            private String referenceNumber;

            public String getReferenceNumber() {
                return referenceNumber;
            }

            public void setReferenceNumber(String referenceNumber) {
                this.referenceNumber = referenceNumber;
            }
        }
    }
}
