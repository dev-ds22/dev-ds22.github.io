package com.learnjava.custom.rule.engine;

/**
 * Generic exception thrown for any rule related runtime exception.
 * 
 * @author MuthukumaranN
 *
 */
@SuppressWarnings("serial")
public class RuleException extends RuntimeException {

	public RuleException() {
		// TODO Auto-generated constructor stub
	}

	public RuleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RuleException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public RuleException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
