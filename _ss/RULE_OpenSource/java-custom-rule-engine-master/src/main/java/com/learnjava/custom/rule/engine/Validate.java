package com.learnjava.custom.rule.engine;

/**
 * Validate class has common validation.
 * 
 * @author MuthukumaranN
 *
 */
public abstract class Validate{
	
	/**
	 * Validates given object is not null; otherwise, throws RuleException with the message.
	 * 
	 * @param data to validate.
	 * @param message to be thrown as RuleException.
	 * 
	 */
	public static void isNotNull(Object data, String message){
		if(data == null)
			throw new RuleException(message);
	}
	
	/**
	 * Validates given object is null; otherwise, throws RuleException with the message.
	 * 
	 * @param data to validate.
	 * @param message to be thrown as RuleException.
	 * 
	 */
	public static void isNull(Object data, String message){
		if(data != null)
			throw new RuleException(message);
	}
	
	public static void isTrue(boolean condition, String message) {
		if(!condition)
			throw new RuleException(message);
	}
	
	public static void isNotTrue(boolean condition, String message) {
		if(condition)
			throw new RuleException(message);
	}
	
	public static void throwError(String message) {
		throw new RuleException(message);
	}
	
}
