package com.simple.bank.utils;

import org.springframework.beans.factory.annotation.Value;

public class Constants {

    public static String DEPOSIT = "DEPOSIT";
    public static String WITHDRAW= "WITHDRAW";
    public static String TRANSFER = "TRANSFER";

    public static String CURRENT = "CURRENT";
    public static String SAVINGS = "SAVINGS";

    public static String SAVING_ACCOUNT_INTEREST_REMARKS = "Savings Acc Interest";
    public static String LOAN_PROCESSED_REMARKS = "Loan processed";
    public static String LOAN_INTEREST_REMARKS = "Interest on loan";

    public static String TRANSACTIONS_STATUS_STARTED = "STARTED";
    public static String TRANSACTIONS_STATUS_SUCCESS = "SUCCESS";
    public static String TRANSACTION_STATUS_FAILED = "FAILURE";

    public static String LOAN_APPLICATION_STATUS_ACCEPTED = "ACCEPTED";
    public static String LOAN_APPLICATION_STATUS_REJECTED = "REJECTED";

    public static String LOAN_AMOUNT_TOO_LOW = "LOAN AMOUNT TOO LOW";
    public static String LOAN_AMOUNT_TOO_HIGH = "LOAN AMOUNT TOO HIGH";
    public static String CREDIT_SCORE_TOO_LOW = "CREDIT SCORE TOO LOW";
    public static String HAS_EXISTING_LOAN_OR_RED_FLAGS = "HAS EXISTING LOAN OR RED FLAGS";

}
