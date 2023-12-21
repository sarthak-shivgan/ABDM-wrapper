package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnConfirmResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Logs;
import com.nha.abdm.wrapper.hrp.mongo.tables.TokenManagement;
import com.nha.abdm.wrapper.hrp.repository.TokenManagementRepo;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.SignedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.Token;
import org.slf4j.helpers.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Service
public class TokenManagementService {
    @Autowired
    TokenManagementRepo tokenManagementRepo;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    Utils utils;
    private static final Logger log = LogManager.getLogger(TokenManagementService.class);

    public void setToken(String abhaAddress,OnConfirmResponse data) throws Exception {
        String token=data.getAuth().getAccessToken();
        Query query = new Query(Criteria.where("abhaAddress").is(abhaAddress));
        TokenManagement existingRecord = mongoTemplate.findOne(query, TokenManagement.class);
        if (existingRecord == null) {
            TokenManagement newRecord = new TokenManagement(abhaAddress,token,getExpiry(token));
            mongoTemplate.insert(newRecord);
        } else {
            Update update = (new Update()).set("x-token", data.getAuth().getAccessToken());
            mongoTemplate.updateFirst(query, update, Logs.class);
        }
    }
    public Date getExpiry(String token) throws Exception {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getExpiresAt();
    }
    public String fetchToken(String abhaAddress) throws ParseException {
        Query query = new Query(Criteria.where("abhaAddress").is(abhaAddress));
        TokenManagement existingRecord = mongoTemplate.findOne(query, TokenManagement.class);
        if(existingRecord!=null){
            if((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(utils.getCurrentTimeStamp()).compareTo(existingRecord.getExpiry())<0)) return existingRecord.getToken();
        }
        return null;
    }
}
