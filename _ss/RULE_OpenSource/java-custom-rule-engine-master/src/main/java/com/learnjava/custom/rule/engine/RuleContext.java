package com.learnjava.custom.rule.engine;

import java.util.HashMap;
import java.util.List;

/**
 * Defines Rule Context. Serves as a working memory for currently executing session.
 * 
 * @author MuthukumaranN
 *
 */
public interface RuleContext{
	
	/**
	 * Gets fact list.
	 * 
	 * @return
	 */
	List<Object> getFactList();
	
	/**
	 * Returns true if any error is present. 
	 * 
	 * @return
	 */
	boolean hasError();
	
	/**
	 * Returns the errors.
	 * 
	 * @return
	 */
	List<Error> getErrors();
	
	/**
	 * Adds error.
	 * 
	 * @param error
	 */
	void addError(Error error);
	
	/**
	 * Adds errors.
	 * 
	 * @param error
	 */
	void addErrors(List<Error> error);
	
	/**
	 * Returns rule execution status.
	 * 
	 * @return
	 */
	RuleExecutionStatus getRuleExecutionStatus();
	
	/**
	 * Sets the rule execution status.
	 * 
	 * @param ruleExecutionStatus
	 */
	void setRuleExecutionStatus(RuleExecutionStatus ruleExecutionStatus);
	
	/**
	 * Returns the rules.
	 * 
	 * @return
	 */
	List<Rule> getRules();
	
	/**
	 * Returns the currently running rule.
	 * 
	 * @return
	 */
	Rule getRule();
	
	/**
	 * Returns the previously run rule.
	 * 
	 * @return
	 */
	Rule getPreviousRule();
	
	/**
	 * Returns the next rule that will be fire after this.
	 * 
	 * @return
	 */
	Rule getNextRule();
	
	/**
	 * Returns the global data map.
	 * 
	 * @return
	 */
	HashMap<String, Object> getGlobalData();
	
	/**
	 * Puts the key/value pair to the map.
	 * 
	 * @param key
	 * @param value
	 */
	void putGlobalData(String key, Object value);
	
	/**
	 * Returns global value for the key.
	 * 
	 * @param key
	 * @return
	 */
	Object getGlobalData(String key);
	
	/**
	 * Sets the suppressed exception.
	 * 
	 * @param runtimeException
	 */
	void setException(RuntimeException runtimeException);

}
