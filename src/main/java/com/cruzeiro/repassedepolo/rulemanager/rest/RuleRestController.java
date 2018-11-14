package com.cruzeiro.repassedepolo.rulemanager.rest;

import com.cruzeiro.repassedepolo.rulemanager.entities.RuleState;
import com.cruzeiro.repassedepolo.rulemanager.entities.Response;
import com.cruzeiro.repassedepolo.rulemanager.entities.commands.RuleCmd;
import com.cruzeiro.repassedepolo.rulemanager.rest.dto.RuleRequest;
import com.cruzeiro.repassedepolo.rulemanager.services.RulesBroker;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * The main order domain REST API
 *
 * @author romeh
 */

@RestController
@RequestMapping("/orders")
@Api(value = "Order Manager REST API demo")
public class RuleRestController {

	@Autowired
	private RulesBroker rulesBroker;

	/**
	 * @param ruleRequest json order request
	 * @return ASYNC generic JSON response
	 */
	@RequestMapping(method = RequestMethod.POST)
	public CompletableFuture<Response> createRule(@RequestBody @Valid RuleRequest ruleRequest) {
		return rulesBroker.createOrder(new RuleCmd.CreateCmd(UUID.randomUUID().toString(), ruleRequest.getOrderDetails()));

	}

	/**
	 * @param validateCmd validate order command JSON
	 * @return ASYNC generic JSON response
	 */
	@RequestMapping(value = "validate", method = RequestMethod.POST)
	public CompletableFuture<Response> validateRule(@RequestBody @Valid RuleCmd.ValidateCmd validateCmd) {
		return rulesBroker.validateRule(validateCmd);

	}

	/**
	 * @param signCmd sign order command JSON
	 * @return ASYNC generic JSON response
	 */
	@RequestMapping(value = "sign", method = RequestMethod.POST)
	public CompletableFuture<Response> signRule(@RequestBody @Valid RuleCmd.SignCmd signCmd) {
		return rulesBroker.signeRule(signCmd);

	}

	/**
	 * @param ruleId unique ruleId string value
	 * @return ASYNC RuleState Json response
	 */
	@RequestMapping(value = "/{ruleId}", method = RequestMethod.GET)
	public CompletableFuture<RuleState> getRuleState(@PathVariable @NotNull String ruleId) {
		return rulesBroker.getRuleStatus(new RuleCmd.GetRuleStatusCmd(ruleId, Collections.emptyMap()));

	}

}
