package com.spring.akka.eventsourcing.persistance;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * Generic Error response class
 */
@Builder
@Value
public class ErrorResponse implements Serializable {
	private String errorMsg;
	private String errorCode;
	private String exceptionMsg;
}
