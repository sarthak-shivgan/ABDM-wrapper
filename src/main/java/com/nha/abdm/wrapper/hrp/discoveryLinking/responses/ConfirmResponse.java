package com.nha.abdm.wrapper.hrp.discoveryLinking.responses;

public class ConfirmResponse {
    public String requestId;
    public DiscoverResponse.ErrorData error;
    public DiscoverResponse.ErrorData getError() {
        return error;
    }

    public void setError(DiscoverResponse.ErrorData error) {
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

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public Confirmation confirmation;
    public class Confirmation{
        String linkRefNumber;

        public String getLinkRefNumber() {
            return linkRefNumber;
        }

        public void setLinkRefNumber(String linkRefNumber) {
            this.linkRefNumber = linkRefNumber;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        String token;
    }
}
