package com.nha.abdm.wrapper.hrp.discoveryLinking.responses;

public class ConfirmResponse {
    public String requestId;

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
