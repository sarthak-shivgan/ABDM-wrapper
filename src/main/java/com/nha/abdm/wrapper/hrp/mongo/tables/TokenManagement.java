package com.nha.abdm.wrapper.hrp.mongo.tables;

import org.apache.tomcat.util.json.Token;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "tokenManagement")
public class TokenManagement {
    @Field("abhaAddress")
    @Indexed(unique = true)
    public String abhaAddress;
    @Field("x-token")
    public String token;

    @Field("expiry")
    public Date expiry;

    public TokenManagement(String abhaAddress, String token, Date expiry){
        this.abhaAddress=abhaAddress;
        this.token=token;
        this.expiry=expiry;

    }

    public String getAbhaAddress() {
        return abhaAddress;
    }

    public void setAbhaAddress(String abhaAddress) {
        this.abhaAddress = abhaAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }


}
