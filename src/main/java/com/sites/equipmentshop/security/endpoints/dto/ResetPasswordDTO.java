package com.sites.equipmentshop.security.endpoints.dto;

import javax.validation.constraints.NotNull;

public class ResetPasswordDTO {

	@NotNull(message = "Reset id cannot be null")
	private String resetId;

	@NotNull
	private String newPassword;

	public String getResetId() {
		return resetId;
	}

	public void setResetId(String resetId) {
		this.resetId = resetId;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
