package com.learnjava.custom.rule.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of Rule Context.
 * 
 * @author MuthukumaranN
 *
 */
public class RuleContextImpl implements MutableRuleContext {

	private List<Object> factList;
	private List<Rule> rules;
	private Rule rule;
	private Rule previousRule;
	private Rule nextRule;
	private RuntimeException exception;
	RuleExecutionStatus ruleExecutionStatus;
	List<Error> errors = new ArrayList<>();
	HashMap<String, Object> globalData = new HashMap<>();

	public RuleContextImpl() {

	}

	public List<Object> getFactList() {
		return factList;
	}

	public void setFactList(List<Object> factList) {
		this.factList = factList;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public Rule getPreviousRule() {
		return previousRule;
	}

	public void setPreviousRule(Rule previousRule) {
		this.previousRule = previousRule;
	}

	public Rule getNextRule() {
		return nextRule;
	}

	public void setNextRule(Rule nextRule) {
		this.nextRule = nextRule;
	}

	public RuntimeException getException() {
		return exception;
	}

	public void setException(RuntimeException exception) {
		this.exception = exception;
	}

	public RuleExecutionStatus getRuleExecutionStatus() {
		return ruleExecutionStatus;
	}

	public void setRuleExecutionStatus(RuleExecutionStatus ruleExecutionStatus) {
		this.ruleExecutionStatus = ruleExecutionStatus;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public HashMap<String, Object> getGlobalData() {
		return globalData;
	}

	public void setGlobalData(HashMap<String, Object> globalData) {
		this.globalData = globalData;
	}
	
	public void putGlobalData(String key, Object value) {
		if(globalData == null)
			globalData = new HashMap<>();
		globalData.put(key, value);
	}
	
	public Object getGlobalData(String key) {
		if(globalData == null)
			return null;
		return globalData.get(key);
	}

	@Override
	public boolean hasError() {
		return errors != null && errors.size() > 0;
	}

	@Override
	public void addError(Error error) {
		this.errors.add(error);
	}

	@Override
	public void addErrors(List<Error> error) {
		this.errors.addAll(errors);
	}

}
