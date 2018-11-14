package com.spring.akka.eventsourcing.persistance.eventsourcing.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;


/**
 * main class for the action result that need to be done from the command context after executing the command handler
 *
 * @param <E> the event type
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Persist<E> {

	private E event;
	private Consumer<E> afterPersist;
}
