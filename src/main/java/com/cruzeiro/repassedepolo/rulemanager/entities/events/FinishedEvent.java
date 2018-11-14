package com.cruzeiro.repassedepolo.rulemanager.entities.events;


import com.cruzeiro.repassedepolo.rulemanager.entities.enums.RuleStatus;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class FinishedEvent extends RuleEvent {

	public FinishedEvent(String ruleId, RuleStatus ruleStatus) {
		super(ruleId, ruleStatus);
	}

}
