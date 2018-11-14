package com.cruzeiro.repassedepolo.rulemanager.entities.events;


import com.cruzeiro.repassedepolo.rulemanager.entities.enums.RuleStatus;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ValidatedEvent extends RuleEvent {

	public ValidatedEvent(String ruleId, RuleStatus ruleStatus) {
		super(ruleId, ruleStatus);
	}

}