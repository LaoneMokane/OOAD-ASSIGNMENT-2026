package banking;

import java.util.List;
import java.util.UUID;

public final class AccountController {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public AccountController(AccountRepository accounts,
                             TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public List<Account> accountsFor(Customer customer) {
        return accounts.findByCustomer(customer);
    }

    public Account openAccount(Customer customer, String type,
                               double openingBalance, String branch,
                               String employerName, String employerAddress) {
        String number = "BW" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();

        Account account;

        if ("SAVINGS".equals(type)) {
            account = new SavingsAccount(
                    number, openingBalance, branch, customer
            );
        } else if ("INVESTMENT".equals(type)) {
            account = new InvestmentAccount(
                    number, openingBalance, branch, customer
            );
        } else if ("CHEQUE".equals(type)) {
            account = new ChequeAccount(
                    number, openingBalance, branch, customer,
                    employerName, employerAddress
            );
        } else {
            throw new IllegalArgumentException("Choose an account type.");
        }

        accounts.save(account);
        customer.openAccount(account);
        return account;
    }

    public Account deposit(Customer customer, String number, double amount) {
        return deposit(customer, number, amount, "Money deposited");
    }

    public Account deposit(Customer customer, String number, double amount,
                           String description) {
        return transact(customer, number, amount, true, description);
    }

    public Account withdraw(Customer customer, String number, double amount) {
        return withdraw(customer, number, amount, "Money withdrawn");
    }

    public Account withdraw(Customer customer, String number, double amount,
                            String description) {
        return transact(customer, number, amount, false, description);
    }

    private Account transact(Customer customer, String number, double amount,
                             boolean isDeposit, String description) {
        Account account = accounts.findByNumber(customer, number)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found."));

        double previousBalance = account.getBalance();
        int previousTransactionCount = account.getTransactions().size();

        try {
            if (isDeposit) {
                account.deposit(amount, description);
            } else {
                account.withdraw(amount, description);
            }

            List<Transactions> history = account.getTransactions();
            Transactions latest = history.get(history.size() - 1);

            accounts.saveBalanceAndTransaction(account, latest);
            return account;
        } catch (RuntimeException e) {
            account.restoreBalance(previousBalance);

            while (account.getTransactions().size()
                    > previousTransactionCount) {
                int lastIndex = account.getTransactions().size() - 1;
                account.removeTransaction(
                        account.getTransactions().get(lastIndex)
                );
            }

            throw e;
        }
    }

    public List<TransactionRepository.TransactionRow> history(String number) {
        return transactions.findByAccount(number);
    }

    public int applyInterest(Customer customer) {
        return new InterestScheduler(accounts).applyToCustomer(customer);
    }
}