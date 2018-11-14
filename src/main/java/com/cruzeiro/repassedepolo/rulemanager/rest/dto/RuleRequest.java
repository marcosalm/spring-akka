package com.cruzeiro.repassedepolo.rulemanager.rest.dto;

import lombok.Data;

import java.util.Map;

/**
 * order request json object for rest API
 *
 * @author romeh
 */
@Data
public class RuleRequest {

	Map<String, String> ruleDetails;
}
