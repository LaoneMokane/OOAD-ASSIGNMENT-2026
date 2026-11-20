package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account implements AccountOperations {
    private final String accountNO;
    private final String branch;
    private final Customer customer;
    private double balance;
    private final List<Transactions> transactions = new ArrayList<>();

    protected Account(String accountNO, double openingBalance,
                      String branch, Customer customer) {
        if (accountNO == null || accountNO.isBlank()) {
            throw new IllegalArgumentException("Account number is required.");
        }
        if (branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Branch is required.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Invalid opening balance.");
        }

        this.accountNO = accountNO.trim();
        this.balance = round(openingBalance);
        this.branch = branch.trim();
        this.customer = customer;
    }

    public String getAccountNO() {
        return accountNO;
    }

    public String getBranch() {
        return branch;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void deposit(double amount) {
        deposit(amount, "Money deposited");
    }

    public void deposit(double amount, String description) {
        requirePositive(amount);
        balance = round(balance + amount);

        new Transactions(
                "DEPOSIT",
                amount,
                cleanDescription(description, "Money deposited"),
                this
        ).save();
    }

    @Override
    public void withdraw(double amount) {
        withdraw(amount, "Money withdrawn");
    }

    public void withdraw(double amount, String description) {
        requirePositive(amount);

        if (balance < round(amount)) {
            throw new IllegalStateException("Not enough money in account.");
        }

        balance = round(balance - amount);

        new Transactions(
                "WITHDRAWAL",
                amount,
                cleanDescription(description, "Money withdrawn"),
                this
        ).save();
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
        if (transaction != null && !transactions.contains(transaction)) {
            transactions.add(transaction);
        }
    }

    void removeTransaction(Transactions transaction) {
        transactions.remove(transaction);
    }

    void restoreBalance(double value) {
        balance = round(value);
    }

    protected void creditInterest(double amount) {
        requirePositive(amount);
        balance = round(balance + amount);

        new Transactions(
                "INTEREST",
                amount,
                "Monthly interest credited",
                this
        ).save();
    }

    protected static void requirePositive(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }
    }

    private static String cleanDescription(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value.trim();
    }

    protected static double round(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}