package com.cruzeiro.repassedepolo.rulemanager.entities.events;

import com.cruzeiro.repassedepolo.rulemanager.entities.enums.RuleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class RuleEvent implements Serializable {
	private String ruleId;
	private RuleStatus ruleStatus;

}
