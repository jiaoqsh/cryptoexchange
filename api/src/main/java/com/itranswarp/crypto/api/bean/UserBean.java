package com.itranswarp.crypto.api.bean;

import io.swagger.annotations.ApiModelProperty;

public class UserBean {

	@ApiModelProperty(value = "User email", example = "test@example.com")
	public String email;

	@ApiModelProperty(value = "User name", example = "Test Bob")
	public String name;

	@ApiModelProperty(value = "SHA-1 password", example = "7c4a8d09ca3762af61e59520943dc26494f8941b")
	public String passwd;

}
