package com.learnjava.custom.rule.engine;

/**
 * Implementation of rule. 
 * 
 * @author MuthukumaranN
 *
 */
public class RuleImpl implements Rule {
	
	String name;
	RuleCondition ruleCondition;

	public void setName(String name) {
		this.name = name;
	}

	public void setRuleCondition(RuleCondition ruleCondition) {
		this.ruleCondition = ruleCondition;
	}

	public RuleImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public RuleCondition getRuleCondition() {
		return ruleCondition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleImpl other = (RuleImpl) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
