package com.cruzeiro.repassedepolo.rulemanager.serializer;

import akka.serialization.SerializerWithStringManifest;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.cruzeiro.repassedepolo.rulemanager.entities.events.RuleEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import com.romeh.ordermanager.protobuf.EventsAndCommands;

import java.io.NotSerializableException;

/**
 * @author romeh
 */
public class RuleManagerSerializer extends SerializerWithStringManifest {


	private static final String ORDER_EVENT = "orderEvent";
	private static final String ORDER_COMMAND = "orderCommand";


	@Override
	public int identifier() {
		return 100;
	}

	@Override
	public String manifest(Object o) {
		if (o instanceof RuleCmd)
			return ORDER_COMMAND;
		else if (o instanceof RuleEvent)
			return ORDER_EVENT;
		else
			throw new IllegalArgumentException("Unknown type: " + o);
	}

	@Override
	public byte[] toBinary(Object o) {

		if(o instanceof RuleEvent){
			RuleEvent ruleEvent =(RuleEvent)o;
			return EventsAndCommands.OrderEvent.newBuilder()
					.setOrderId(ruleEvent.getOrderId())
					.setOrderStatus(ruleEvent.getOrderStatus().name())
					.build().toByteArray();

		}else if(o instanceof RuleCmd){
			RuleCmd orderCmd=(RuleCmd) o;
			return EventsAndCommands.OrderCmd.newBuilder()
					.setOrderId(orderCmd.getOrderId())
					.putAllOrderDetails(orderCmd.getOrderDetails())
					.build().toByteArray();
		}else{
			throw new IllegalArgumentException("Cannot serialize object of type " + o.getClass().getName());
		}

	}

	@Override
	public Object fromBinary(byte[] bytes, String manifest) throws NotSerializableException {
		try {
			if (manifest.equals(ORDER_COMMAND)) {

				return EventsAndCommands.OrderCmd.parseFrom(bytes);

			} else if (manifest.equals(ORDER_EVENT)) {
				return EventsAndCommands.OrderEvent.parseFrom(bytes);
			} else {
				throw new NotSerializableException(
						"Unimplemented deserialization of message with manifest [" + manifest + "] in " + getClass().getName());
			}
		}catch (InvalidProtocolBufferException e) {
			throw new NotSerializableException(e.getMessage());
		}

	}
}
