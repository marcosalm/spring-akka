package com.cruzeiro.repassedepolo.rulemanager.domain;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import com.cruzeiro.repassedepolo.rulemanager.entities.RuleState;
import com.cruzeiro.repassedepolo.rulemanager.entities.Response;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.cruzeiro.repassedepolo.rulemanager.entities.enums.RuleStatus;
import com.cruzeiro.repassedepolo.rulemanager.entities.events.*;
import com.spring.akka.eventsourcing.config.PersistentEntityProperties;
import com.spring.akka.eventsourcing.persistance.AsyncResult;
import com.spring.akka.eventsourcing.persistance.eventsourcing.ExecutionFlow;
import com.spring.akka.eventsourcing.persistance.eventsourcing.FlowContext;
import com.spring.akka.eventsourcing.persistance.eventsourcing.PersistentEntity;
import com.spring.akka.eventsourcing.persistance.eventsourcing.ReadOnlyFlowContext;
import com.spring.akka.eventsourcing.persistance.eventsourcing.actions.Persist;
import com.spring.akka.eventsourcing.persistance.eventsourcing.annotations.PersistentActor;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static akka.actor.SupervisorStrategy.*;

/**
 * Tha main Event sourcing DDD aggregate class for rule domain which handle the rule commands within it is boundary context
 *
 * @author romeh
 */
@PersistentActor
public class RuleManager extends PersistentEntity<RuleCmd, RuleEvent, RuleState> {

	/**
	 * how to handle supervisor strategy definition for the parent actor of the entity
	 */
	private static SupervisorStrategy strategy =
			new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES), DeciderBuilder.
					match(ArithmeticException.class, e -> resume()).
					match(NullPointerException.class, e -> restart()).
					match(IllegalArgumentException.class, e -> stop()).
					matchAny(o -> escalate()).build());

	/**
	 * @param persistentEntityConfig the akka persistent entity configuration
	 */
	@Autowired
	public RuleManager(PersistentEntityProperties<RuleManager, RuleCmd, RuleEvent> persistentEntityConfig) {
		super(persistentEntityConfig);
	}

	/**
	 * @param state the current State
	 * @return the initialized behavior for the entity
	 */
	@Override
	protected ExecutionFlow<RuleCmd, RuleEvent, RuleState> executionFlow(RuleState state) {
		switch (state.getRuleStatus()) {
			case NotStarted:
				return notStarted(state);
			case Created:
				return waitingForValidation(state);
			case Validated:
				return waitingForSigning(state);
			case Signed:
				return complected(state);
			case COMPLETED:
				return complected(state);
			default:
				throw new IllegalStateException();

		}
	}

	@Override
	protected RuleState initialState() {
		return new RuleState(Collections.emptyList(), RuleStatus.NotStarted);
	}

	/**
	 * ExecutionFlow for the not started state.
	 */
	private ExecutionFlow<RuleCmd, RuleEvent, RuleState> notStarted(RuleState state) {
		final ExecutionFlow.ExecutionFlowBuilder<RuleCmd, RuleEvent, RuleState> executionFlowBuilder = newFlowBuilder(state);

		// Command handlers
		executionFlowBuilder.onCommand(RuleCmd.CreateCmd.class, (start, ctx, currentState) ->
				persistAndReply(ctx, new CreatedEvent(start.getRuleId(), RuleStatus.Created))
		);

		// Event handlers
		executionFlowBuilder.onEvent(CreatedEvent.class, (started, currentState) ->
				createImmutableState(state, started, RuleStatus.Created)
		);

		return executionFlowBuilder.build();
	}

	/**
	 * ExecutionFlow for the not created and not yet validated.
	 */

	private ExecutionFlow<RuleCmd, RuleEvent, RuleState> waitingForValidation(RuleState state) {
		final ExecutionFlow.ExecutionFlowBuilder<RuleCmd, RuleEvent, RuleState> executionFlowBuilder = newFlowBuilder(state);
		// Command handlers
		executionFlowBuilder.onCommand(RuleCmd.ValidateCmd.class, (start, ctx, currentState) ->
				persistAndReply(ctx, new ValidatedEvent(start.getRuleId(), RuleStatus.Validated))
		);
		// Read only command handlers
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.CreateCmd.class, this::alreadyDone);
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.SignCmd.class, this::NotAllowed);
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.GetRuleStatusCmd.class, (cmd, ctx) -> ctx.reply(getState()));

		// Event handlers
		executionFlowBuilder.onEvent(ValidatedEvent.class, (validated, currentState) ->
				createImmutableState(state, validated, validated.getRuleStatus())
		);

		return executionFlowBuilder.build();
	}

	/**
	 * ExecutionFlow for the not validated and not yet signed.
	 */
	private ExecutionFlow<RuleCmd, RuleEvent, RuleState> waitingForSigning(RuleState state) {
		final ExecutionFlow.ExecutionFlowBuilder<RuleCmd, RuleEvent, RuleState> executionFlowBuilder = newFlowBuilder(state);
		// Command handlers
		executionFlowBuilder.onCommand(RuleCmd.SignCmd.class, (start, ctx, currentState) ->
				persistAndReply(ctx, new SignedEvent(start.getRuleId(), RuleStatus.Signed))
		);
		// Async Command handler
		executionFlowBuilder.asyncOnCommand(RuleCmd.AsyncSignCmd.class, (signed, ctx, currentState) -> CompletableFuture
				.supplyAsync(() -> AsyncResult.<RuleEvent>builder()
						.persist(persistAndReply(ctx, new SignedEvent(signed.getRuleId(), RuleStatus.Signed)))
						.build())
		);
		// Read only command handlers
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.GetRuleStatusCmd.class, (cmd, ctx) -> ctx.reply(getState()));
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.ValidateCmd.class, this::alreadyDone);
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.CreateCmd.class, this::alreadyDone);
		// Event handlers
		executionFlowBuilder.onEvent(SignedEvent.class, (signed, currentState) ->
				createImmutableState(state, signed, signed.getRuleStatus())
		);

		return executionFlowBuilder.build();
	}

	/**
	 * ExecutionFlow for signed and final state
	 */
	private ExecutionFlow<RuleCmd, RuleEvent, RuleState> complected(RuleState state) {
		final ExecutionFlow.ExecutionFlowBuilder<RuleCmd, RuleEvent, RuleState> executionFlowBuilder = newFlowBuilder(state);
		// just read only command handlers as it is final state
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.GetRuleStatusCmd.class, (cmd, ctx) -> ctx.reply(getState()));
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.CreateCmd.class, this::alreadyDone);
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.ValidateCmd.class, this::alreadyDone);
		executionFlowBuilder.onReadOnlyCommand(RuleCmd.SignCmd.class, this::alreadyDone);
		// Event handlers
		executionFlowBuilder.onEvent(FinishedEvent.class, (finished, currentState) ->
				createImmutableState(state, finished, finished.getRuleStatus())
		);

		return executionFlowBuilder.build();
	}

	/**
	 * @param testState   current state
	 * @param testEvent   new event
	 * @param ruleStatus new rule status
	 * @return immutable state
	 */
	private RuleState createImmutableState(RuleState testState, RuleEvent testEvent, RuleStatus ruleStatus) {
		final List<RuleEvent> eventsHistory = new ArrayList<>(testState.getEventsHistory());
		eventsHistory.add(testEvent);
		return new RuleState(eventsHistory, ruleStatus);

	}

	/**
	 * Persist a single event then respond with done.
	 */
	private Persist<RuleEvent> persistAndDone(FlowContext ctx, RuleEvent event) {
		return ctx.thenPersist(event, (e) -> ctx.reply(Response.builder().ruleId(event.getRuleId()).responseMsg("successfully executed").ruleStatus(event.getRuleStatus().name()).build()));
	}

	/**
	 * Persist a single event then respond with done.
	 */
	private Persist<RuleEvent> persistAndReply(FlowContext ctx, RuleEvent event) {
		return ctx.thenPersist(event, (e) -> ctx.reply(Response.builder().ruleStatus(event.getRuleStatus().name()).ruleId(event.getRuleId()).build()));
	}

	/**
	 * Convenience method to handle when a command has already been processed (idempotent processing).
	 */
	private void alreadyDone(RuleCmd cmd, ReadOnlyFlowContext ctx) {
		ctx.reply(Response.builder().ruleId(cmd.getRuleId()).responseMsg("the command is already done and applied before").build());
	}

	/**
	 * Convenience method to handle when a command has is not allowed based into rule state.
	 */
	private void NotAllowed(RuleCmd cmd, ReadOnlyFlowContext ctx) {
		ctx.reply(Response.builder().ruleId(cmd.getRuleId()).errorMessage("the request action is not allowed for the current rule statue").errorCode("1111").build());
	}

	/**
	 * @return supervisorStrategy the actor supervisor strategy
	 */
	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

}
