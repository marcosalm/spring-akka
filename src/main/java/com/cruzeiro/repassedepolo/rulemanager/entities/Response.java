package com.cruzeiro.repassedepolo.rulemanager.entities;


import lombok.Builder;
import lombok.Data;

/**
 * generic service response object
 */
@Data
@Builder
public class Response {

	private String ruleId;
	private String ruleStatus;
	private String errorCode;
	private String responseMsg;
	private String errorMessage;

}
