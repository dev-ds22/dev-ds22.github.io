package com.learnjava.custom.rule.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Core of Rule Engine that builds Rule Context and executes the added rule(s) for the supplied fact(s). 
 * 
 * @author MuthukumaranN
 *
 */
public class RuleExecutorImpl implements RuleExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(RuleExecutorImpl.class);

	private List<Object> factList = new ArrayList<>();
	private LinkedHashSet<Rule> rules;
	RuleContext ruleContext;
	HashMap<String, Object> globalData = new HashMap<>();

	public RuleExecutorImpl() {
		// TODO Auto-generated constructor stub
	}

	public List<Object> getFactList() {
		return factList;
	}

	public void setFactList(List<Object> factList) {
		Validate.isNotNull(factList, "Invalid factList : " + factList);
		this.factList = factList;
	}

	public LinkedHashSet<Rule> getRules() {
		return rules;
	}

	public void setRules(LinkedHashSet<Rule> rules) {
		this.rules = rules;
	}

	public RuleContext getRuleContext() {
		return ruleContext;
	}

	public void setRuleContext(RuleContext ruleContext) {
		this.ruleContext = ruleContext;
	}

	public HashMap<String, Object> getGlobalData() {
		return globalData;
	}

	public void setGlobalData(HashMap<String, Object> globalData) {
		Validate.isNotNull(globalData, "Invalid globalData : " + globalData);
		this.globalData = globalData;
	}

	public void putGlobalData(String key, Object value) {
		Validate.isNotNull(key, "Invalid key : " + key);
		if (globalData == null)
			globalData = new HashMap<>();
		globalData.put(key, value);
	}

	public Object getGlobalData(String key) {
		if (globalData == null)
			return null;
		return globalData.get(key);
	}

	@Override
	public boolean hasError() {
		return ruleContext.hasError();
	}

	@Override
	public void insertFact(Object fact) {
		Validate.isNotNull(fact, "Invalid fact : " + fact);
		this.factList.add(fact);
	}

	@Override
	public void insertFacts(List<Object> facts) {
		Validate.isNotNull(facts, "Invalid facts : " + facts);
		this.factList.addAll(facts);
	}

	@Override
	public List<Error> getErrors() {
		return this.ruleContext.getErrors();
	}

	@Override
	public void fireRule() {
		Validate.isNotTrue((rules == null || rules.size() == 0), "No rule found.");
		Validate.isNotTrue(rules.size() > 1, "More than one rule found. Please use fireAllRules.");
		logBefore();
		MutableRuleContext mutableRuleContext = buildRuleContext();
		mutableRuleContext.setRuleExecutionStatus(RuleExecutionStatus.RUNNING);
		List<Rule> ruleList = getRulesAsList();
		AtomicInteger counter = new AtomicInteger(0);
		factList.stream().forEach(fact -> {
			LOG.info("****************************************************************************************");
			LOG.info("Started firing Rule(s) for Fact(#{}) : {} ", counter.incrementAndGet(), fact);
			LOG.info("****************************************************************************************\n");
			Rule rule = ruleList.get(0);
			mutableRuleContext.setRule(rule);
			fireRule(rule, fact);
			LOG.info("Completed running Rule(s)\n\n");
		});
		ruleContext.setRuleExecutionStatus(RuleExecutionStatus.COMPLETED);
		LOG.info("Total No of Rules : {}", ruleContext.getRules().size());
		LOG.info("Total No of Facts : {}", ruleContext.getFactList().size());
		logAfter();
	}

	@Override
	public void fireAllRules() {
		Validate.isNotTrue((rules == null || rules.size() == 0), "No rule found.");
		logBefore();
		MutableRuleContext mutableRuleContext = buildRuleContext();
		mutableRuleContext.setRuleExecutionStatus(RuleExecutionStatus.RUNNING);
		List<Rule> ruleList = getRulesAsList();
		AtomicInteger counter = new AtomicInteger(0);
		factList.stream().forEach(fact -> {
			LOG.info("****************************************************************************************");
			LOG.info("Started firing Rule(s) for Fact(#{}) : {} ", counter.incrementAndGet(), fact);
			LOG.info("****************************************************************************************\n");
			getRulesAsList().forEach(rule -> {
				mutableRuleContext.setRule(rule);
				mutableRuleContext
						.setPreviousRule(ruleList.indexOf(rule) > 0 ? ruleList.get(ruleList.indexOf(rule) - 1) : null);
				mutableRuleContext.setNextRule(
						ruleList.indexOf(rule) < (ruleList.size() - 1) ? ruleList.get(ruleList.indexOf(rule) + 1)
								: null);
				fireRule(rule, fact);
			});
			LOG.info("Completed running Rule(s)\n\n");
		});
		mutableRuleContext.setRuleExecutionStatus(RuleExecutionStatus.COMPLETED);
		LOG.info("Total No of Rules : {}", ruleContext.getRules().size());
		LOG.info("Total No of Facts : {}", ruleContext.getFactList().size());
		logAfter();

	}

	private void fireRule(Rule rule, Object fact) {
		LOG.info("<<< Firing Rule : {} >>>", rule.getName());
		rule.getRuleCondition().execute(ruleContext, fact);
		LOG.info("<<< Completed Rule : {} >>>\n", rule.getName());
	}

	private MutableRuleContext buildRuleContext() {
		ruleContext = RuleContextBuilder.newRuleContext().withRules(new ArrayList<Rule>(rules)).withFacts(factList)
				.withGlobalData(globalData).build();
		return getMutableRuleContext();
	}

	private MutableRuleContext getMutableRuleContext() {
		return (MutableRuleContext) ruleContext;
	}

	private List<Rule> getRulesAsList() {
		return new ArrayList<Rule>(rules);
	}

	private void logBefore() {
		LOG.info("------------------------------------------------------------------");
		LOG.info("   		    RULE EXECUTION STARTED                              ");
		LOG.info("------------------------------------------------------------------\n\n");
	}

	private void logAfter() {
		LOG.info("------------------------------------------------------------------");
		LOG.info("   		   RULE EXECUTION COMPLETED                             ");
		LOG.info("------------------------------------------------------------------");
	}

}
