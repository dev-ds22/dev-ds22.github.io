package com.learnjava.custom.rule.engine;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankAccountMain {

	private static final Logger LOG = LoggerFactory.getLogger(BankAccountMain.class);

	public static void main(String args[]) {
		
		BankAccount account1 = new BankAccount("0000000100","John",new BigDecimal("1000.00"), EnumAccountType.SAVINGS);
		BankAccount account2 = new BankAccount("0000000200","Smith",new BigDecimal("9000.00"), EnumAccountType.CURRENT);
		BankAccount account3 = new BankAccount("0000000300","Wayne",new BigDecimal("5000.00"), EnumAccountType.MONEY_MARKET_ACCOUNT);
		
		RuleExecutor ruleExecutor = RuleExecutorBuilder.newRuleExecutor()
		.addRule(calculateInterestAmt()) // Adding Rule
		.addRule(calculateYearlyMainteneceFee()) // Adding Rule
		.build();
		
		//Inserting Facts
		ruleExecutor.insertFacts(Arrays.asList(account1,account2,account3));
		
		//Justing printing details, before firing rules.
		LOG.info("Justing printing details, before firing rules.");
		printBankDetails(Arrays.asList(account1,account2,account3));
		
		//Firing Rules
		ruleExecutor.fireAllRules();
		
		//Justing printing details, after firing rules.
		LOG.info("Justing printing details, after firing rules.");
		printBankDetails(Arrays.asList(account1,account2,account3));
		
	}
	
	public static Rule calculateInterestAmt() {
		return RuleBuilder.newRuleBuilder().withName("Inerest Calculation Rule")
				.withCondition((RuleContext ruleContext, Object fact) -> {
					BankAccount account = (BankAccount) fact;
					BigDecimal rate = new BigDecimal("0");
					switch(account.accountType) {
					case SAVINGS:
						rate = new BigDecimal("3");
						break;
					case CURRENT:
						rate = new BigDecimal("2");
						break;
					case MONEY_MARKET_ACCOUNT:	
						rate = new BigDecimal("5");
						break;
					}
					BigDecimal interestAmt = account.balance.multiply((rate.divide(new BigDecimal("100"))));
					
					account.interestAmt = interestAmt;
					account.interestRate = rate;
					
				}).build();
	}
	
	public static Rule calculateYearlyMainteneceFee() {
		return RuleBuilder.newRuleBuilder().withName("Yearly Account Maintenance Fee")
				.withCondition((RuleContext ruleContext, Object fact) -> {
					BankAccount account = (BankAccount) fact;
					BigDecimal yearMaintenanceFee = new BigDecimal("0");
					switch(account.accountType) {
					case SAVINGS:
						yearMaintenanceFee = new BigDecimal("100");
						break;
					case CURRENT:
						yearMaintenanceFee = new BigDecimal("50");
						break;
					case MONEY_MARKET_ACCOUNT:	
						yearMaintenanceFee = new BigDecimal("0");
						break;
					}
					
					account.maintenenceFee = yearMaintenanceFee;
					
				}).build();
	}
	
	public static void printBankDetails(List<BankAccount> accList) {
		accList.stream().forEach(account ->{
			LOG.info("Account Holder Name {}", account.accountHolderName);
			LOG.info("Account Interest Rate {}", account.interestRate);
			LOG.info("Account Interest Amt {}", account.interestAmt);
			LOG.info("Account Maintenance Fee {} \n", account.maintenenceFee);
		});
		
	}

}
