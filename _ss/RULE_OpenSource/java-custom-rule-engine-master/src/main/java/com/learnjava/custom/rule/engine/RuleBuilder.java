package com.learnjava.custom.rule.engine;

import java.util.Random;

/**
 * Rule Builder builds Rule.
 * 
 * @author MuthukumaranN
 *
 */
public class RuleBuilder {
	
	String name;
	RuleCondition ruleCondition;

	private RuleBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates instance of rule builder and return the reference.
	 * 
	 * @return
	 */
	public static RuleBuilder newRuleBuilder() {
		return new RuleBuilder();	
	}
	
	/**
	 * Builds new rule for the rule name and condition.
	 * 
	 * @param ruleName
	 * @param ruleCondition
	 * @return
	 */
	public static Rule newRule(String ruleName, RuleCondition ruleCondition) {
		RuleBuilder ruleBuilder = new RuleBuilder();
		return ruleBuilder.withName(ruleName).withCondition(ruleCondition).build();
	}
	
	/**
	 * Builds new rule for the condition.
	 * 
	 * @param ruleName
	 * @param ruleCondition
	 * @return
	 */
	public static Rule newRule(RuleCondition ruleCondition) {
		RuleBuilder ruleBuilder = new RuleBuilder();
		return ruleBuilder.withCondition(ruleCondition).build();
	}
	
	/**
	 * With name.
	 * 
	 * @param ruleName
	 * @return
	 */
	public RuleBuilder withName(String ruleName) {
		this.name = ruleName;
		return this;
	}
	
	/**
	 * With condition.
	 * 
	 * @param ruleCondition
	 * @return
	 */
	public RuleBuilder withCondition(RuleCondition ruleCondition) {
		this.ruleCondition = ruleCondition;
		return this;
	}
	
	/**
	 * Builds rule.
	 * 
	 * @return
	 */
	public Rule build() {
		RuleImpl rule = new RuleImpl();
		//When no name found, it auto generates one. 
		if(name == null) {
			name = "Rule "+Math.abs((new Random()).nextLong());
		}
		rule.setName(name);
		rule.setRuleCondition(ruleCondition);
		return rule;
	}
	
}
