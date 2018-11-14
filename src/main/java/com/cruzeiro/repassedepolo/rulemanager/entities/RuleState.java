package com.cruzeiro.repassedepolo.rulemanager.entities;

import com.cruzeiro.repassedepolo.rulemanager.entities.enums.RuleStatus;
import com.cruzeiro.repassedepolo.rulemanager.entities.events.RuleEvent;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * the main immutable order state object
 */
@Value
public class RuleState implements Serializable {

	private final List<RuleEvent> eventsHistory;
	private final RuleStatus ruleStatus;

	public RuleState(List<RuleEvent> eventsHistory, RuleStatus ruleStatus) {
		this.eventsHistory = eventsHistory;

		this.ruleStatus = ruleStatus;
	}


	public RuleStatus getRuleStatus() {
		return this.ruleStatus;
	}
}
