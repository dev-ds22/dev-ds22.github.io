package com.learnjava.custom.rule.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Builds Rule Executor.
 * 
 * @author MuthukumaranN
 *
 */
public class RuleExecutorBuilder {

	private List<Object> factList = new ArrayList<>();
	private LinkedHashSet<Rule> rules = new LinkedHashSet<>();
	@SuppressWarnings("unused")
	private HashMap<String, Object> globalData = new HashMap<>();

	private RuleExecutorBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates and return the instance of Rule Executor.
	 * 
	 * @return
	 */
	public static RuleExecutorBuilder newRuleExecutor() {
		return new RuleExecutorBuilder();
	}

	/**
	 * Adds the rule.
	 * 
	 * @param rule
	 * @return
	 */
	public RuleExecutorBuilder addRule(Rule rule) {
		Validate.isNotNull(rule, "Invalid rule : " + rule);
		this.rules.add(rule);
		return this;
	}

	/**
	 * Adds the rules.
	 * 
	 * @param rules
	 * @return
	 */
	public RuleExecutorBuilder addRules(List<Rule> rules) {
		Validate.isNotNull(rules, "Invalid rules : " + rules);
		this.rules.addAll(rules);
		return this;
	}

	/**
	 * With global data.
	 * 
	 * @param globalData
	 * @return
	 */
	public RuleExecutorBuilder withGloblData(HashMap<String, Object> globalData) {
		this.globalData = globalData;
		return this;
	}

	/**
	 * Inserts the fact for the rules to be fired.
	 * 
	 * @param fact
	 * @return
	 */
	public RuleExecutorBuilder insertFact(Object fact) {
		Validate.isNotNull(fact, "Invalid fact " + fact);
		this.factList.add(fact);
		return this;
	}

	/**
	 * Inserts the facts for the rules to be fired.
	 * 
	 * @param facts
	 * @return
	 */
	public RuleExecutorBuilder insertFacts(List<Object> facts) {
		Validate.isNotNull(facts, "Invalid facts : " + facts);
		this.factList.addAll(facts);
		return this;
	}

	/**
	 * Builds Rule Executor based on the supplied rule executor data.
	 * 
	 * @return
	 */
	public RuleExecutor build() {
		RuleExecutorImpl ruleExecutor = new RuleExecutorImpl();
		ruleExecutor.setFactList(factList);
		ruleExecutor.setRules(rules);
		return ruleExecutor;
	}

}
