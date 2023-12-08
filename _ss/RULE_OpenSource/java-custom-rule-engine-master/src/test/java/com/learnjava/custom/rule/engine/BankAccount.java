package com.learnjava.custom.rule.engine;

import java.math.BigDecimal;

public class BankAccount {

	String accountId;
	String accountHolderName;
	BigDecimal balance;
	EnumAccountType accountType;
	BigDecimal interestAmt;
	BigDecimal interestRate;
	BigDecimal maintenenceFee;
	
	public BankAccount() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param accountId
	 * @param accountHolderName
	 * @param balance
	 * @param accountType
	 */
	public BankAccount(String accountId, String accountHolderName, BigDecimal balance, EnumAccountType accountType) {
		super();
		this.accountId = accountId;
		this.accountHolderName = accountHolderName;
		this.balance = balance;
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "BankAccount [accountId=" + accountId + ", accountHolderName=" + accountHolderName + "]";
	}

}
