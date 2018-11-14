package com.cruzeiro.repassedepolo.rulemanager.services;

import akka.actor.ActorRef;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.cruzeiro.repassedepolo.rulemanager.domain.RuleManager;
import com.cruzeiro.repassedepolo.rulemanager.entities.RuleState;
import com.cruzeiro.repassedepolo.rulemanager.entities.Response;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.spring.akka.eventsourcing.persistance.eventsourcing.PersistentEntityBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * the orders service that handle order commands and respond in async mode
 *
 * @author romeh
 */
@Service
public class RulesBroker {

	/**
	 * the AKKA sharding persistent entities general broker
	 */
	private final PersistentEntityBroker persistentEntityBroker;
	private final static Timeout timeout=Timeout.apply(
			2, TimeUnit.SECONDS);
	/**
	 * generic completable future handle response function
	 */
	private final Function<Object, Response> handlerResponse = o -> {

		if (o != null && o instanceof Response) {
			return (Response) o;
		}
		else {
			return Response.builder().errorCode("1100").errorMessage("unexpected error has been found").build();
		}

	};

	/**
	 * generic completable future get rule state handle response function
	 */
	private final Function<Object, RuleState> handleGetState = o -> {

		if (o != null) {
			return (RuleState) o;
		} else {
			throw new IllegalStateException("un-expected error has been thrown");
		}

	};
	/**
	 * generic completable future handle exception function
	 */
	private final Function<Throwable, Response> handleException = throwable -> Response.builder().errorCode("1111").errorMessage(throwable.getLocalizedMessage()).build();


	@Autowired
	public RulesBroker(PersistentEntityBroker persistentEntityBroker) {
		this.persistentEntityBroker = persistentEntityBroker;
	}

	/**
	 * create rule service API
	 *
	 * @param createCmd create rule command
	 * @return generic response object
	 */
	public CompletableFuture<Response> createRule(RuleCmd.CreateCmd createCmd) {

		return PatternsCS.ask(getRuleEntity(), createCmd, timeout).toCompletableFuture()
				.thenApply(handlerResponse).exceptionally(handleException);
	}

	/**
	 * validate rule service API
	 *
	 * @param validateCmd validate rule command
	 * @return generic response object
	 */
	public CompletableFuture<Response> validateRule(RuleCmd.ValidateCmd validateCmd) {
		return PatternsCS.ask(getRuleEntity(), validateCmd, timeout).toCompletableFuture()
				.thenApply(handlerResponse).exceptionally(handleException);
	}

	/**
	 * Sign rule service API
	 *
	 * @param signCmd sign rule command
	 * @return generic response object
	 */
	public CompletableFuture<Response> signeRule(RuleCmd.SignCmd signCmd) {
		return PatternsCS.ask(getRuleEntity(), signCmd, timeout).toCompletableFuture()
				.thenApply(handlerResponse).exceptionally(handleException);
	}

	/**
	 * get rule state service API
	 *
	 * @param getRuleStatusCmd get Rule state command
	 * @return rule state
	 */
	public CompletableFuture<RuleState> getRuleStatus(RuleCmd.GetRuleStatusCmd getRuleStatusCmd) {
		return PatternsCS.ask(getRuleEntity(), getRuleStatusCmd, timeout).toCompletableFuture()
				.thenApply(handleGetState);
	}

	/**
	 * @return Persistent entity actor reference based into AKKA cluster sharding
	 */
	private final ActorRef getRuleEntity() {
		return persistentEntityBroker.findPersistentEntity(RuleManager.class);

	}


}
