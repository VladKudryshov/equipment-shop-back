package com.sites.equipmentshop.security.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sites.equipmentshop.security.domain.LoggedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserDTOSerializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginUserDTOSerializer.class);

	private final ObjectMapper mapper;

	public LoginUserDTOSerializer() {
		this.mapper = new ObjectMapper();
	}

	public String serialize (LoggedUser dto) {
		try {
			return mapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			LOGGER.warn("LoginUserDTO serialization failed: ", e);
		}
		return null;
	}
}
