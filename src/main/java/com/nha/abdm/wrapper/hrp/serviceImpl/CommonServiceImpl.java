package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.common.GatewayApiPaths;
import com.nha.abdm.wrapper.hrp.common.SessionManager;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.common.Utils;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import com.nha.abdm.wrapper.hrp.services.CommonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CommonServiceImpl implements CommonServices {
	@Autowired
	SessionManager sessionManager;
	@Autowired
	ApplicationConfig applicationConfig;
	private static final Logger log = LogManager.getLogger(CommonServiceImpl.class);


	public String startSession() throws JsonProcessingException, URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestBody = mapper.createObjectNode();
		requestBody.put("clientId", applicationConfig.clientId);
		requestBody.put("clientSecret", applicationConfig.clientSecret);
		String json = mapper.writeValueAsString(requestBody);
		HttpEntity<String> requestEntity = new HttpEntity(json, headers);
		try{
			WebClient.Builder webClientBuilder = WebClient.builder();
			ResponseEntity<ObjectNode> responseEntity = webClientBuilder
					.build()
					.post()
					.uri(GatewayApiPaths.CREATE_SESSION)
					.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
					.body(BodyInserters.fromValue(requestEntity.getBody()))
					.retrieve()
					.toEntity(ObjectNode.class)
					.block();
			String accessToken= MessageFormat.format("Bearer {0}",responseEntity.getBody().findValue("accessToken").asText());
			if(accessToken!=null){
				sessionManager.setAccessToken(accessToken);
				return accessToken;
			}else{
				log.info(GatewayApiPaths.CREATE_SESSION+" : "+responseEntity.getBody().toString());
			}
		}catch(Exception e){
			log.error("Unable to generate accessToken: "+ e);
		}
		return null;


	}


}
