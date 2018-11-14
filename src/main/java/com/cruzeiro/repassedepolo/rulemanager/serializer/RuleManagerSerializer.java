package com.cruzeiro.repassedepolo.rulemanager.serializer;

import akka.serialization.SerializerWithStringManifest;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.cruzeiro.repassedepolo.rulemanager.entities.events.RuleEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import com.cruzeiro.repassedepolo.rulemanager.protobuf.EventsAndCommands;

import java.io.NotSerializableException;

/**
 *
 */
public class RuleManagerSerializer extends SerializerWithStringManifest {


	private static final String RULE_EVENT = "ruleEvent";
	private static final String RULE_COMMAND = "ruleCommand";


	@Override
	public int identifier() {
		return 100;
	}

	@Override
	public String manifest(Object o) {
		if (o instanceof RuleCmd)
			return RULE_COMMAND;
		else if (o instanceof RuleEvent)
			return RULE_EVENT;
		else
			throw new IllegalArgumentException("Unknown type: " + o);
	}

	@Override
	public byte[] toBinary(Object o) {

		if(o instanceof RuleEvent){
			RuleEvent ruleEvent =(RuleEvent)o;
			return EventsAndCommands.RuleEvent.newBuilder()
					.setRuleId(ruleEvent.getRuleId())
					.setRuleStatus(ruleEvent.getRuleStatus().name())
					.build().toByteArray();

		}else if(o instanceof RuleCmd){
			RuleCmd ruleCmd=(RuleCmd) o;
			return EventsAndCommands.RuleCmd.newBuilder()
					.setRuleId(ruleCmd.getRuleId())
					.putAllRuleDetails(ruleCmd.getRuleDetails())
					.build().toByteArray();
		}else{
			throw new IllegalArgumentException("Cannot serialize object of type " + o.getClass().getName());
		}

	}

	@Override
	public Object fromBinary(byte[] bytes, String manifest) throws NotSerializableException {
		try {
			if (manifest.equals(RULE_COMMAND)) {

				return EventsAndCommands.RuleCmd.parseFrom(bytes);

			} else if (manifest.equals(RULE_EVENT)) {
				return EventsAndCommands.RuleEvent.parseFrom(bytes);
			} else {
				throw new NotSerializableException(
						"Unimplemented deserialization of message with manifest [" + manifest + "] in " + getClass().getName());
			}
		}catch (InvalidProtocolBufferException e) {
			throw new NotSerializableException(e.getMessage());
		}

	}
}
