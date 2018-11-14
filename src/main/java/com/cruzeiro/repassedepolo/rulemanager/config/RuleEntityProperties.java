package com.cruzeiro.repassedepolo.rulemanager.config;

import com.cruzeiro.repassedepolo.rulemanager.domain.RuleManager;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.cruzeiro.repassedepolo.rulemanager.entities.events.*;
import com.spring.akka.eventsourcing.config.PersistentEntityProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * the main order entity required configuration for event souring toolkit
 */
@Component
public class RuleEntityProperties implements PersistentEntityProperties<RuleManager, RuleCmd, RuleEvent> {

	private Map<Class<? extends RuleEvent>, String> tags;

	/**
	 * init the event tags map
	 */
	@PostConstruct
	public void init() {
		Map<Class<? extends RuleEvent>, String> init = new HashMap<>();
		init.put(CreatedEvent.class, "CreatedOrders");
		init.put(FinishedEvent.class, "FinishedOrders");
		init.put(SignedEvent.class, "SignedOrders");
		init.put(ValidatedEvent.class, "ValidatedOrders");
		tags = Collections.unmodifiableMap(init);

	}

	/**
	 * @return the entity should save snapshot of its state after how many persisted events
	 */
	@Override
	public int snapshotStateAfter() {
		return 5;
	}

	/**
	 * @return the entity should passivate after how long of being non actively serving requests
	 */
	@Override
	public long entityPassivateAfter() {
		return 60;
	}

	/**
	 * @return map of event class to tag value , used into event tagging before persisting the events into the event store by akka persistence
	 */
	@Override
	public Map<Class<? extends RuleEvent>, String> tags() {

		return tags;
	}

	/**
	 * @return number of cluster sharding for the entity
	 */
	@Override
	public int numberOfShards() {
		return 20;
	}

	/**
	 * @return persistenceIdPostfix function , used in cluster sharding entity routing
	 */
	@Override
	public Function<RuleCmd, String> persistenceIdPostfix() {
		return RuleCmd::getRuleId;
	}

	/**
	 * @return entity persistenceIdPrefix , used in cluster sharding routing
	 */
	@Override
	public String persistenceIdPrefix() {
		return RuleManager.class.getSimpleName();
	}

	/**
	 * @return entity class
	 */
	@Override
	public Class<RuleManager> getEntityClass() {
		return RuleManager.class;
	}

	/**
	 * @return main super root command type
	 */
	@Override
	public Class<RuleCmd> getRootCommandType() {
		return RuleCmd.class;
	}

	/**
	 * @return main super root event type
	 */
	@Override
	public Class<RuleEvent> getRootEventType() {
		return RuleEvent.class;
	}


	@Override
	public String asyncPersistentEntityDispatcherName() {
		return null;
	}

	/**
	 * @return the custom configurable dispatcher name used for async blocking IO operations in the persistent entity,
	 * to be used instead of the default akka actors dispatchers to not starve the actors thread dispatcher with blocking IO
	 */
	@Override
	public String pipeDispatcherName() {
		return null;
	}

	/**
	 * @return the timeout for any async command handler actions , used to monitor pipeTo actions
	 */
	@Override
	public long scheduledAsyncEntityActionTimeout() {
		return 3;
	}
}
