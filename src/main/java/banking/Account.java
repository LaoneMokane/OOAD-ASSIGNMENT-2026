package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account implements AccountOperations {
    private final String accountNO;
    private double balance;
    private final Customer customer;
    private final List<Transactions> transactions = new ArrayList<>();

    protected Account(String accountNO, double openingBalance,
                      String branch, Customer customer) {
        if (accountNO == null || accountNO.isBlank()
                 || customer == null) {
            throw new IllegalArgumentException(
                field + "is required."
            );
        }
        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Invalid opening balance.");
        }

        this.accountNO = accountNO;
        this.balance = openingBalance;
        this.customer = customer;
    }

    public String getAccountNO()
     { return accountNO; }
    public Customer getCustomer() 
    { return customer; }
     
public abstract String getAccountType();

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void deposit(double amount) {
        requirePositive(amount);
        balance = round(balance + amount);
        addTransaction("DEPOSIT", amount, "Money deposited");
    }

    @Override
    public void withdraw(double amount) {
        requirePositive(amount);
        if (balance < amount) {
            throw new IllegalStateException("Not enough money in account.");
        }
        balance = round(balance - amount);
        addTransactionTransaction("WITHDRAWAL", amount, "Money withdrawn");
    }

    public abstract String getAccountType();

    public List<Transactions> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public String getTransaction() {
        StringBuilder result = new StringBuilder();
        for (Transactions transaction : transactions) {
            result.append(transaction.getDetails())
                  .append(System.lineSeparator());
        }
        return result.toString();
    }

    void addTransaction(Transactions transaction) {
        if (!transactions.contains(transaction)) {
            transactions.add(transaction);
        }
    }

    protected void creditInterest(double amount) {
        requirePositive(amount);
        balance = round(balance + amount);
        recordTransaction("INTEREST", amount, "Monthly interest credited");
    }

    private void recordTransaction(String type, double amount,
                                   String description) {
        new Transactions(type, amount, description, this).save();
    }

    protected static void requirePositive(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero."
            );
        }
    }

    protected static double round(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}