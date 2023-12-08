package com.learnjava.custom.rule.engine;

import java.util.HashMap;
import java.util.List;

/**
 * Defines Mutable Rule Context. Serves as a working memory for currently executing session.
 * 
 * @author MuthukumaranN
 *
 */
public interface MutableRuleContext extends RuleContext{
	
	/**
	 * Sets the fact list.
	 * 
	 * @param fact
	 */
	void setFactList(List<Object> fact);
	
	/**
	 * Sets the rules.
	 * 
	 * @param rules
	 */
	void setRules(List<Rule> rules);
	
	/**
	 * Sets the rule.
	 * 
	 * @param rule
	 */
	void setRule(Rule rule);
	
	/**
	 * Sets the previous rule.
	 * 
	 * @param previousRule
	 */
	void setPreviousRule(Rule previousRule);
	
	/**
	 * Sets the next rule.
	 * 
	 * @param nextRule
	 */
	void setNextRule(Rule nextRule);
	
	/**
	 * Sets the global data map.
	 * 
	 * @param globalData
	 */
	void setGlobalData(HashMap<String, Object> globalData);
	
	/**
	 * Gets the suppressed exception.
	 * 
	 * @return
	 */
	RuntimeException getException();
}
