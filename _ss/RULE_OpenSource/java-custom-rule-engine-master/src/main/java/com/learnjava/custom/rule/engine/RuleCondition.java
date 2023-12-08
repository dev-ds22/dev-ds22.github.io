package com.learnjava.custom.rule.engine;

/**
 * Rule Condition is a functional interface. It defines the Rule Condition that needs to be executed at runtime.
 * 
 * @author MuthukumaranN
 *
 */
@FunctionalInterface
public interface RuleCondition{
	
	/**
	 * Executes the rule condition with the supplied fact.
	 * 
	 * @param ruleContext
	 * @param fact
	 */
	public void execute(RuleContext ruleContext, Object fact);
	
}
