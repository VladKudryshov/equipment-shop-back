package com.sites.equipmentshop.security.services;

import com.sites.equipmentshop.security.endpoints.dto.NewUserDTO;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import org.antlr.stringtemplate.StringTemplate;
import org.flywaydb.core.internal.util.FileCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class MailTemplateFactory {
	
	private static final String TEMPLATE_FOLDER = "classpath:templates/";
	
	@Value("${sem.platform.domain.url}")
	private String domain;
	private final String resetTemplate;
	private final String signTemplate;
	
	@Autowired
	public MailTemplateFactory(ResourceLoader resourceLoader) throws IOException {
		Resource resetTemlRes =  resourceLoader.getResource(TEMPLATE_FOLDER + "reset_message.st");
		this.resetTemplate = FileCopyUtils.copyToString(new InputStreamReader(resetTemlRes.getInputStream(), StandardCharsets.UTF_8));
		Resource signTemlRes =  resourceLoader.getResource(TEMPLATE_FOLDER + "sign_message.st");
		this.signTemplate = FileCopyUtils.copyToString(new InputStreamReader(signTemlRes.getInputStream(), StandardCharsets.UTF_8));
	}

	public String prepareResetPassTemplate(UserEntity entity, String token) {
		StringTemplate template = new StringTemplate(resetTemplate);
		template.setAttribute("user_name", entity.getUserName());	
		template.setAttribute("user_email", entity.getEmail());	
		template.setAttribute("platformUrl", domain);	
		template.setAttribute("token", token);	
		return template.toString();
	}
	
	public String prepareSignTemplate(NewUserDTO userDto, String password) {
		StringTemplate template = new StringTemplate(signTemplate);
		template.setAttribute("user_name", userDto.getUserName());
		template.setAttribute("user_email", userDto.getEmail());
		template.setAttribute("user_password", password);
		template.setAttribute("platformUrl", domain);
		return template.toString();
	}
	
}
