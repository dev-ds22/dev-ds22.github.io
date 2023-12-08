package com.learnjava.custom.rule.engine;

import java.util.HashMap;
import java.util.List;

/**
 * The Core of Rule Engine that builds Rule Context and executes the added rule(s) for the supplied fact(s). 
 * 
 * @author MuthukumaranN
 *
 */
public interface RuleExecutor {
	
	/**
	 * Fires the rule.
	 */
	void fireRule();
	
	/**
	 * Fires all the rules.
	 * 
	 */
	void fireAllRules();
	
	/**
	 * Returns true if any error is found. 
	 * 
	 * @return
	 */
	boolean hasError();
	
	/**
	 * Returns the captured errors.
	 * 
	 * @return
	 */
	List<Error> getErrors();
	
	/**
	 * Inserts the fact for the rules to be fired.
	 * 
	 * @param fact
	 */
	void insertFact(Object fact);
	
	/**
	 * 
	 * @param facts
	 */
	void insertFacts(List<Object> facts);
	
	/**
	 * Inserts the facts for the rules to be fired.
	 * 
	 * @param globalData
	 */
	void setGlobalData(HashMap<String, Object> globalData);
	
	/**
	 * Puts the key/value pair to the global data map.
	 * 
	 * @param key
	 * @param value
	 */
	public void putGlobalData(String key, Object value);

}
