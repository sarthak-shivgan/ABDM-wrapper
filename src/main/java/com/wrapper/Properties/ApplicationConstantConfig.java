//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:application.properties"})
public class ApplicationConstantConfig {
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

	public ApplicationConstantConfig() {
	}
}
