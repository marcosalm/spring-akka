package com.spring.akka.eventsourcing.config;


import com.spring.akka.eventsourcing.persistance.eventsourcing.PersistentEntity;

import java.util.Map;
import java.util.function.Function;

public interface PersistentEntityProperties<A extends PersistentEntity, C, E> {
	int snapshotStateAfter();

	long entityPassivateAfter();

	Map<Class<? extends E>, String> tags();

	int numberOfShards();

	Function<C, String> persistenceIdPostfix();

	String persistenceIdPrefix();

	Class<A> getEntityClass();

	Class<C> getRootCommandType();

	Class<E> getRootEventType();

	String asyncPersistentEntityDispatcherName();

	String pipeDispatcherName();

	long scheduledAsyncEntityActionTimeout();

}
