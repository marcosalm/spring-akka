package com.cruzeiro.repassedepolo.rulemanager.entities.commands;

import lombok.Value;

import java.io.Serializable;
import java.util.Map;


public interface RuleCmd extends Serializable {

	String getRuleId();

	Map<String, String> getRuleDetails();

	@Value
	final class CreateCmd implements RuleCmd {
		private String ruleId;
		private Map<String, String> ruleDetails;
	}

	@Value
	final class ValidateCmd implements RuleCmd {
		private String ruleId;
		private Map<String, String> ruleDetails;
	}

	@Value
	final class SignCmd implements RuleCmd {
		private String ruleId;
		private Map<String, String> ruleDetails;
	}

	@Value
	final class GetRuleStatusCmd implements RuleCmd {
		private String ruleId;
		private Map<String, String> ruleDetails;
	}

	@Value
	final class AsyncSignCmd implements RuleCmd {
		private String ruleId;
		private Map<String, String> ruleDetails;
	}
}
