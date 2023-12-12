package com.nha.abdm.wrapper.hrp.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:application.properties"})
public class ApplicationConfig {
	@Value("${clientId}")
	public String clientId;
	@Value("${clientSecret}")
	public String clientSecret;
	@Value("${createSession}")
	public String url;
	@Value("${link_authInit}")
	public String link_authInit;
	@Value("${link_ConfirmAuth}")
	public String link_ConfirmAuth;
	@Value("${link_addCareContext}")
	public String link_addCareContext;

	@Value("${onDiscover}")
	public String onDiscover;
	@Value("${onInit}")
	public String onInit;

	@Value("${onConfirm}")
	public String onConfirm;

	@Value("${environment}")
	public String enviornment;
}
