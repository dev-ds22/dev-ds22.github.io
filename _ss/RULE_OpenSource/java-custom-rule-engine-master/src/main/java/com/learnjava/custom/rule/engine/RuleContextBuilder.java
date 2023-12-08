package com.learnjava.custom.rule.engine;

import java.util.HashMap;
import java.util.List;

/**
 * Rule Context Builder builds Rule Context.
 * 
 * @author MuthukumaranN
 *
 */
public class RuleContextBuilder {

	private List<Rule> rules;
	private Rule rule;
	private List<Object> factList;
	RuleExecutionStatus ruleExecutionStatus;
	HashMap<String, Object> globalData = new HashMap<>();
	
	private RuleContextBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public static RuleContextBuilder newRuleContext() {
		return new RuleContextBuilder();
	}
	
	public RuleContextBuilder withRule(Rule rule) {
		Validate.isNotNull(rules, "Invalid rule : "+rule);
		this.rule = rule;
		return this;
	}
	
	public RuleContextBuilder withRules(List<Rule> rules) {
		Validate.isNotNull(rules, "Invalid rules : "+rules);
		this.rules = rules;
		return this;
	}
	
	public RuleContextBuilder withFacts(List<Object> factList) {
		Validate.isNotNull(factList, "Invalid factList : "+factList);
		this.factList = factList;
		return this;
	}
	
	public RuleContextBuilder withGlobalData(HashMap<String, Object> globalData ) {
		Validate.isNotNull(globalData, "Invalid globalData : "+globalData);
		this.globalData.putAll(globalData);
		return this;
	} 
	
	public RuleContextBuilder withRuleExecutionStatus(RuleExecutionStatus ruleExecutionStatus) {
		this.ruleExecutionStatus = ruleExecutionStatus;
		return this;
	}
	
	public RuleContext build() {
		MutableRuleContext ruleContext = new RuleContextImpl();
		ruleContext.setRule(rule);
		ruleContext.setRules(rules);
		ruleContext.setRuleExecutionStatus(ruleExecutionStatus);
		ruleContext.setFactList(factList);
		ruleContext.setGlobalData(globalData);
		return ruleContext;
		
	}
}
